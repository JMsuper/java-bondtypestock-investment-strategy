package com.finance.adam.service;

import com.finance.adam.openapi.dart.vo.OpenDartReportExtractedDTO;
import com.finance.adam.repository.financeinfo.dto.FinanceInfoDTO;
import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoResponseDTO;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.vo.DartFinancialInfo;
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
        List<CorpInfo> corpInfos = corpRepository.findAllWithStockPrice();
        List<CorpInfo> nullCheckedCorpInfos = new ArrayList<>();
        for(CorpInfo corpInfo : corpInfos){
            if(corpInfo.getStockPrice() == null){
                log.error("StockPrice is null");
                log.error("corpCode: {}, stockCode : {}", corpInfo.getCorpCode(), corpInfo.getStockCode());
            }
            else{
                nullCheckedCorpInfos.add(corpInfo);
            }
        }
        return nullCheckedCorpInfos.stream()
                .map((corpInfo -> KrxCorpListResponse.fromCorpInfo(corpInfo)))
                .collect(Collectors.toList());
    }

    public StockPriceInfoResponseDTO getStockPriceInfo(String stockCode){
        StockPrice stockPrice = stockPriceRepository.findByCorpInfoStockCode(stockCode);
        if(stockPrice == null){
            return null;
        }
        return StockPriceInfoResponseDTO.builder()
                .closingPrice(stockPrice.getClosingPrice())
                .difference(stockPrice.getDifference())
                .fluctuationRate(stockPrice.getFluctuationRate())
                .openingPrice(stockPrice.getOpeningPrice())
                .build();
    }

    public List<FinanceInfoDTO> getFinanceInfos(String corpCode, int startYear, int endYear){
        List<FinanceInfoDTO> financeInfoDTOs = new ArrayList<>();
        for (int year = startYear; year <= endYear; year++) {
            Optional<FinanceInfo> financeInfo = financeInfoRepository.findByCorpInfoCorpCodeAndYear(corpCode, year);
            if (financeInfo.isPresent()) {
                FinanceInfoDTO financeInfoDTO = FinanceInfoDTO.fromFinanceInfo(financeInfo.get());
                financeInfoDTOs.add(financeInfoDTO);
            }
        }
        return financeInfoDTOs;
    }


    public List<OpenDartReportExtractedDTO> getReports(String corpCode){
        List<OpenDartReportExtractedDTO> reportList = openDartAPI.getRecentReportList(corpCode, 5)
                .stream().map((report) -> OpenDartReportExtractedDTO.from(report))
                .toList();
        return reportList;
    }

    /**
     * - 연결 재무제표일 경우, CFS 라는 key 를 갖음(value는 0L) <br>
     * - 재무제표일 경우, OFS 라는 key 를 갖음(value는 0L)
     */
    public Map<String, Long> getFinancialInfoFromDart(String corpCode, String bsnsYear){
        List<DartFinancialInfo> corpFinancialInfoList = openDartAPI.getCorpFinancialInfo(corpCode, bsnsYear);
        if(corpFinancialInfoList == null){
            return null;
        }

        // 연결 재무제표
        Map<String,Long> CFS = new HashMap<>();
        CFS.put("CFS",0L);

        // 재무제표
        Map<String,Long> OFS = new HashMap<>();
        OFS.put("OFS",0L);

        for(int i = 0; i < corpFinancialInfoList.size(); i++){
            DartFinancialInfo info = corpFinancialInfoList.get(i);
            Map<String, Long> currentFs;
            if(info.getFsNm().equals("연결재무제표")){
                currentFs = CFS;
            }else{
                currentFs = OFS;
            }

            String name = info.getAccountNm();

            try{
                Long amount = Long.parseLong(info.getThstrmAmount().replaceAll(",",""));
                currentFs.put(name, amount);
            }catch (NumberFormatException e){
                log.info(e.getMessage());
                log.info("corpCode : " + corpCode + ", bsnsYear : " + bsnsYear + ", AccountNm : " + name);
            }

        }

        if(CFS.size() == 1){
            return OFS;
        }
        return CFS;
    }

    @Transactional
    public void renewFinancialInfo(){
        List<CorpInfo> corpInfos = corpRepository.findAll();
        for (CorpInfo corpInfo : corpInfos) {
            String corpCode = corpInfo.getCorpCode();

            int year = Calendar.getInstance().get(Calendar.YEAR) - 1;
            Map<String, Long> info = getFinancialInfoFromDart(corpCode, String.valueOf(year));

            if (info == null) {
                continue;
            }

            Optional<FinanceInfo> v = financeInfoRepository.findByCorpInfoCorpCodeAndYear(corpCode, year);
            if (v.isPresent()) {
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
            log.info("renewFinancialInfo - " + corpInfo.getName() + " " + year + "년도 재무정보 저장");
        }
    }


    /**
     * 수행하는 기능
     * 1. DB 에는 있지만 거래소에서 퇴출된 기업 -> 상장폐지 여부를 true로 변경
     * 2. 종목코드가 변경된 기업 -> 종목코드를 변경
     * 3. DB에 없는 신규 상장 기업 -> DB에 추가
     */
    @Transactional
    public void renewCorpInfoWithKrxList(){
        Map<String, KrxItemInfo> krxItemInfoMap = publicDataPortalOpenAPI.getKrxItemInfoMap();
        Map<String,String> corpCodeMap = openDartAPI.getCorpCodeMap();
        List<CorpInfo> corpInfoList = corpRepository.findAll();

        for(CorpInfo corpInfo : corpInfoList){
            String stockCode = corpInfo.getStockCode();
            String corpCode = corpInfo.getCorpCode();

            // 1. DB 에는 있지만 거래소에서 퇴출된 기업
            KrxItemInfo krxItemInfo = krxItemInfoMap.get(stockCode);
            if(krxItemInfo == null){
                corpInfo.setDeListed(true);
                log.info("거래소 퇴출: " + corpInfo.getName());
            }

            // 2. 종목코드가 변경된 기업
            if(corpCodeMap.containsKey(corpCode)){
                String newStockCode = corpCodeMap.get(corpCode);
                if(!stockCode.equals(newStockCode)){
                    corpInfo.setStockCode("A" + newStockCode);
                    log.info("종목코드 변경: " + corpInfo.getName() + " (before: " + stockCode + ", after: " + newStockCode + ")");
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
                log.info("신규 상장 기업 추가 : " + corpInfo.getName() + ", 종목코드 : " + stockCode);
            }
        }
    }

    public Long getBPS(FinanceInfo financeInfo, StockPrice stockPrice){
        long totalCapital = financeInfo.getTotalCapital();
        long listedShares = stockPrice.getListedShares();

        // 자본총계가 0이거나 상장주식수가 0이면 계산 불가
        if(totalCapital == 0 || listedShares == 0){
            log.error("getBPS - totalCapital or listedShares is 0");
            log.error("corpCode : {}" ,financeInfo.getCorpInfo().getCorpCode());
            return 0L;
        }

        return financeCalculator.calculateBPS(totalCapital, listedShares);
    }

    /**
     * 소숫점 3자리까지 계산
     */
    public Optional<Float> getThreeYearAverageROE(List<FinanceInfo> financeInfoList){
        // 4개 재무정보가 없으면 계산 불가
        if(financeInfoList.size() < 4){
            return Optional.empty();
        }

        float[] roeList = new float[3];

        for(int i = 0; i < 3; i++){
            FinanceInfo thisYearfinanceInfo = financeInfoList.get(i);
            FinanceInfo lastYearfinanceInfo = financeInfoList.get(i + 1);

            if(thisYearfinanceInfo.getNetIncome() == null || lastYearfinanceInfo.getTotalCapital() == null){
                return Optional.empty();
            }

            long netIncome = thisYearfinanceInfo.getNetIncome();
            long lastTotalCapital = lastYearfinanceInfo.getTotalCapital();

            // 당기순이익이 0이거나 자본총계가 0이면 계산 불가
            if(netIncome == 0 || lastTotalCapital == 0){
                return Optional.empty();
            }

            float roe = financeCalculator.calculateROE(netIncome, lastTotalCapital);
            roeList[i] = roe;
        }

        float threeYearAverageROE = (roeList[0] + roeList[1] + roeList[2]) / 3;
        threeYearAverageROE = Math.round(threeYearAverageROE * 1000) / 1000.0f;
        return Optional.of(threeYearAverageROE);
    }
}
