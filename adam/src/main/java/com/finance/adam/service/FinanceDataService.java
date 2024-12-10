package com.finance.adam.service;

import com.finance.adam.openapi.dart.dto.OpenDartReportExtractedDTO;
import com.finance.adam.repository.financeinfo.dto.FinanceInfoDTO;
import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoResponseDTO;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.dto.DartFinancialInfo;
import com.finance.adam.openapi.publicdataportal.PublicDataPortalOpenAPI;
import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.financeinfo.FinanceInfoRepository;
import com.finance.adam.repository.stockprice.StockPriceRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.financeinfo.domain.FinanceInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import com.finance.adam.util.FinanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceDataService {

    private final OpenDartAPI openDartAPI;
    private final PublicDataPortalOpenAPI publicDataPortalOpenAPI;
    private final CorpRepository corpRepository;
    private final FinanceInfoRepository financeInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final FinanceCalculator financeCalculator;

    public List<KrxCorpListResponse> getKrxCorpInfo(){
        log.info("Getting KRX corporation information");
        List<CorpInfo> corpInfos = corpRepository.findAllWithStockPrice();
        log.debug("Found {} corporations with stock price information", corpInfos.size());
        
        List<CorpInfo> nullCheckedCorpInfos = new ArrayList<>();
        for(CorpInfo corpInfo : corpInfos){
            if(corpInfo.getStockPrice() == null){
                log.error("StockPrice is null for corporation: corpCode={}, stockCode={}", corpInfo.getCorpCode(), corpInfo.getStockCode());
            }
            else{
                nullCheckedCorpInfos.add(corpInfo);
            }
        }
        log.debug("{} corporations have valid stock price information", nullCheckedCorpInfos.size());
        return nullCheckedCorpInfos.stream()
                .map((KrxCorpListResponse::fromCorpInfo))
                .collect(Collectors.toList());
    }

    public StockPriceInfoResponseDTO getStockPriceInfo(String stockCode){
        log.info("Getting stock price information for stockCode: {}", stockCode);
        StockPrice stockPrice = stockPriceRepository.findByCorpInfoStockCode(stockCode);
        if(stockPrice == null){
            log.warn("No stock price found for stockCode: {}", stockCode);
            return null;
        }
        log.debug("Found stock price information for stockCode: {}", stockCode);
        return StockPriceInfoResponseDTO.builder()
                .closingPrice(stockPrice.getClosingPrice())
                .difference(stockPrice.getDifference())
                .fluctuationRate(stockPrice.getFluctuationRate())
                .openingPrice(stockPrice.getOpeningPrice())
                .build();
    }

    public List<FinanceInfoDTO> getFinanceInfos(String corpCode, int startYear, int endYear){
        log.info("Getting finance information for corpCode: {} from year {} to {}", corpCode, startYear, endYear);
        List<FinanceInfoDTO> financeInfoDTOs = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            Optional<FinanceInfo> financeInfo = financeInfoRepository.findByCorpInfoCorpCodeAndYear(corpCode, year);
            if (financeInfo.isPresent()) {
                FinanceInfoDTO financeInfoDTO = FinanceInfoDTO.fromFinanceInfo(financeInfo.get());
                financeInfoDTOs.add(financeInfoDTO);
                log.debug("Found finance information for year: {}", year);
            } else {
                log.debug("No finance information found for year: {}", year);
            }
        }
        log.debug("Retrieved {} finance information records", financeInfoDTOs.size());
        return financeInfoDTOs;
    }

    public List<OpenDartReportExtractedDTO> getReports(String corpCode){
        log.info("Getting recent reports for corpCode: {}", corpCode);
        List<OpenDartReportExtractedDTO> reportList = openDartAPI.getRecentReportListFive(corpCode)
                .stream().map(OpenDartReportExtractedDTO::from)
                .toList();
        log.debug("Retrieved {} recent reports", reportList.size());
        return reportList;
    }

    /**
     * - 연결 재무제표일 경우, CFS 라는 key 를 갖음(value는 0L) <br>
     * - 재무제표일 경우, OFS 라는 key 를 갖음(value는 0L)
     */
    public Map<String, Long> getFinancialInfoFromDart(String corpCode, String bsnsYear){
        log.info("Getting financial information from DART for corpCode: {}, year: {}", corpCode, bsnsYear);
        List<DartFinancialInfo> corpFinancialInfoList = openDartAPI.getCorpFinancialInfo(corpCode, bsnsYear);
        if(corpFinancialInfoList == null){
            log.warn("No financial information found from DART");
            return null;
        }

        // 연결 재무제표
        Map<String,Long> CFS = new HashMap<>();
        CFS.put("CFS",0L);

        // 재무제표
        Map<String,Long> OFS = new HashMap<>();
        OFS.put("OFS",0L);

        for (DartFinancialInfo info : corpFinancialInfoList) {
            Map<String, Long> currentFs;
            if (info.getFsNm().equals("연결재무제표")) {
                currentFs = CFS;
            } else {
                currentFs = OFS;
            }

            String name = info.getAccountNm();

            try {
                Long amount = Long.parseLong(info.getThstrmAmount().replaceAll(",", ""));
                currentFs.put(name, amount);
            } catch (NumberFormatException e) {
                log.error("Failed to parse amount for corpCode: {}, year: {}, account: {}", corpCode, bsnsYear, name);
                log.error("Error message: {}", e.getMessage());
            }
        }

        if(CFS.size() == 1){
            log.debug("Using OFS (개별재무제표) as CFS not available");
            return OFS;
        }
        log.debug("Using CFS (연결재무제표)");
        return CFS;
    }

    @Transactional
    public void renewFinancialInfo(){
        log.info("Starting financial information renewal process");
        List<CorpInfo> corpInfos = corpRepository.findAllWithStockPriceAndFinanceInfos();
        log.debug("Found {} corporations to process", corpInfos.size());

        for (CorpInfo corpInfo : corpInfos) {
            String corpCode = corpInfo.getCorpCode();
            int year = Calendar.getInstance().get(Calendar.YEAR) - 1;
            
            log.debug("Processing corporation: {}, year: {}", corpInfo.getName(), year);
            Map<String, Long> info = getFinancialInfoFromDart(corpCode, String.valueOf(year));

            if (info == null) {
                log.warn("No financial information available for corporation: {}", corpInfo.getName());
                continue;
            }

            Optional<FinanceInfo> v = financeInfoRepository.findByCorpInfoCorpCodeAndYear(corpCode, year);
            if (v.isPresent()) {
                log.debug("Financial information already exists for corporation: {}, year: {}", corpInfo.getName(), year);
                continue;
            }

            FinanceInfo financeInfo = FinanceInfo.fromMap(info);
            if (info.containsKey("CFS")) {
                financeInfo.setFsDiv("연결재무제표");
            } else {
                financeInfo.setFsDiv("재무제표");
            }

            financeInfo.setYear(year);
            financeInfo.setCorpInfo(corpInfo);
            financeInfoRepository.save(financeInfo);
            log.info("Saved financial information for corporation: {} year: {}", corpInfo.getName(), year);
        }
        log.info("Completed financial information renewal process");
    }

    /**
     * 수행하는 기능
     * 1. DB 에는 있지만 거래소에서 퇴출된 기업 -> 상장폐지 여부를 true로 변경
     * 2. 종목코드가 변경된 기업 -> 종목코드를 변경
     * 3. DB에 없는 신규 상장 기업 -> DB에 추가
     */
    @Transactional
    public void renewCorpInfoWithKrxList(){
        log.info("Starting corporation information renewal process with KRX list");
        Map<String, KrxItemInfo> krxItemInfoMap = publicDataPortalOpenAPI.getKrxItemInfoMap();
        Map<String,String> corpCodeMap = openDartAPI.getCorpCodeMap();
        List<CorpInfo> corpInfoList = corpRepository.findAllWithStockPrice();
        log.debug("Found {} corporations in database", corpInfoList.size());

        for(CorpInfo corpInfo : corpInfoList){
            String stockCode = corpInfo.getStockCode();
            String corpCode = corpInfo.getCorpCode();

            // 1. DB 에는 있지만 거래소에서 퇴출된 기업
            KrxItemInfo krxItemInfo = krxItemInfoMap.get(stockCode);
            if(krxItemInfo == null){
                corpInfo.setDeListed(true);
                log.info("Corporation delisted: {}", corpInfo.getName());
            }

            // 2. 종목코드가 변경된 기업
            if(corpCodeMap.containsKey(corpCode)){
                String newStockCode = corpCodeMap.get(corpCode);
                if(!stockCode.equals(newStockCode)){
                    corpInfo.setStockCode("A" + newStockCode);
                    log.info("Stock code changed for corporation: {} (from: {} to: {})", 
                            corpInfo.getName(), stockCode, newStockCode);
                }
            }
        }

        // 3. DB에 없는 신규 상장 기업
        for(KrxItemInfo krxItemInfo : krxItemInfoMap.values()){
            String stockCode = krxItemInfo.getSrtnCd();
            boolean isExist = corpInfoList.stream()
                    .anyMatch((corpInfo -> corpInfo.getStockCode().equals(stockCode)));
            if(!isExist){
                CorpInfo corpInfo = CorpInfo.fromKrxItemInfo(krxItemInfo);
                String corpCode = corpCodeMap.get(corpInfo.getParsedStockCode());
                corpInfo.setCorpCode(corpCode);
                corpRepository.save(corpInfo);
                log.info("Added new listed corporation: {}, stockCode: {}", corpInfo.getName(), stockCode);
            }
        }
        log.info("Completed corporation information renewal process");
    }

    public Long getBPS(FinanceInfo financeInfo, StockPrice stockPrice){
        log.debug("Calculating BPS for corporation: {}", financeInfo.getCorpInfo().getName());
        long totalCapital = financeInfo.getTotalCapital();
        long listedShares = stockPrice.getListedShares();

        // 자본총계가 0이거나 상장주식수가 0이면 계산 불가
        if(totalCapital == 0 || listedShares == 0){
            log.error("Cannot calculate BPS - totalCapital or listedShares is 0 for corpCode: {}", 
                    financeInfo.getCorpInfo().getCorpCode());
            return 0L;
        }

        return financeCalculator.calculateBPS(totalCapital, listedShares);
    }

    /**
     * 소숫점 3자리까지 계산
     */
    public Optional<Float> getThreeYearAverageROE(List<FinanceInfo> financeInfoList){
        log.debug("Calculating three-year average ROE");
        // 4개 재무정보가 없으면 계산 불가
        if(financeInfoList.size() < 4){
            log.warn("Insufficient finance information for ROE calculation. Required: 4, Found: {}", 
                    financeInfoList.size());
            return Optional.empty();
        }

        float[] roeList = new float[3];

        for(int i = 0; i < 3; i++){
            FinanceInfo thisYearfinanceInfo = financeInfoList.get(i);
            FinanceInfo lastYearfinanceInfo = financeInfoList.get(i + 1);

            if(thisYearfinanceInfo.getNetIncome() == null || lastYearfinanceInfo.getTotalCapital() == null){
                log.warn("Missing net income or total capital data for ROE calculation");
                return Optional.empty();
            }

            long netIncome = thisYearfinanceInfo.getNetIncome();
            long lastTotalCapital = lastYearfinanceInfo.getTotalCapital();

            // 당기순이익이 0이거나 자본총계가 0이면 계산 불가
            if(netIncome == 0 || lastTotalCapital == 0){
                log.warn("Cannot calculate ROE - netIncome or lastTotalCapital is 0");
                return Optional.empty();
            }

            float roe = financeCalculator.calculateROE(netIncome, lastTotalCapital);
            roeList[i] = roe;
        }

        float threeYearAverageROE = (roeList[0] + roeList[1] + roeList[2]) / 3;
        threeYearAverageROE = Math.round(threeYearAverageROE * 1000) / 1000.0f;
        log.debug("Calculated three-year average ROE: {}", threeYearAverageROE);
        return Optional.of(threeYearAverageROE);
    }
}
