package com.finance.adam.service;

import com.finance.adam.dto.StepOneFinanceInfoDTO;
import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.financeinfo.domain.FinanceInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ScreeningService {

    private final CorpRepository corpRepository;

    public ScreeningService(CorpRepository corpRepository) {
        this.corpRepository = corpRepository;
        log.debug("ScreeningService initialized with CorpRepository");
    }

    public List<StepOneStockInfoDTO> getStepOneStockInfoList() {
        log.info("Getting step one stock info list");
        List<StepOneStockInfoDTO> stepOneStockInfoDTOList = new ArrayList<>();

        List<CorpInfo> corpInfoList = corpRepository.findAllWithStockPriceAndFinanceInfos();
        log.debug("Found {} corporations with stock price and finance info", corpInfoList.size());

        for (CorpInfo corpInfo : corpInfoList) {
            log.debug("Processing corporation: {}", corpInfo.getCorpCode());
            StepOneStockInfoDTO stockInfoDTO = new StepOneStockInfoDTO();
            stockInfoDTO.setStockName(corpInfo.getName());
            stockInfoDTO.setCorpCd(corpInfo.getCorpCode());
            stockInfoDTO.setStockCd(corpInfo.getStockCode());

            StockPrice stockPrice = corpInfo.getStockPrice();
            if(stockPrice == null){
                log.error("StockPrice is null for corporation: corpCode={}, stockCode={}", corpInfo.getCorpCode(), corpInfo.getStockCode());
                continue;
            }
            stockInfoDTO.setOpeningPrice(stockPrice.getOpeningPrice());
            stockInfoDTO.setShares(stockPrice.getListedShares());

            List<FinanceInfo> financeInfoList = corpInfo.getFinanceInfos();
            List<StepOneFinanceInfoDTO> financeInfoDTOList = new ArrayList<>();

            for (FinanceInfo financeInfo : financeInfoList) {
                if(financeInfo.getNetIncome() == null || financeInfo.getTotalCapital() == null){
                    log.warn("Missing financial data for corporation: corpCode={}, year={}", corpInfo.getCorpCode(), financeInfo.getYear());
                    continue;
                }
                StepOneFinanceInfoDTO financeInfoDTO = StepOneFinanceInfoDTO.builder()
                        .year(financeInfo.getYear())
                        .fsDiv(financeInfo.getFsDiv())
                        .totalAsset(financeInfo.getTotalAsset())
                        .totalLiabilities(financeInfo.getTotalLiabilities())
                        .totalCapital(financeInfo.getTotalCapital())
                        .netIncome(financeInfo.getNetIncome())
                        .build();

                financeInfoDTOList.add(financeInfoDTO);
            }

            // 4년치 이상의 재무정보가 없는 경우 제외
            // 3개년 ROE 를 계산하기 위해 4개년 이상의 재무정보가 필요함
            if(financeInfoDTOList.size() < 4){
                log.debug("Insufficient financial data for corporation: corpCode={}, years={}", corpInfo.getCorpCode(), financeInfoDTOList.size());
                continue;
            }

            stockInfoDTO.setFinanceInfoList(financeInfoDTOList);
            stepOneStockInfoDTOList.add(stockInfoDTO);
            log.debug("Successfully processed corporation: {}", corpInfo.getCorpCode());
        }
        log.info("Completed processing step one stock info list. Total valid corporations: {}", stepOneStockInfoDTOList.size());
        return stepOneStockInfoDTOList;
    }
}
