package com.finance.adam.service;

import com.finance.adam.dto.StepFiveStockPriceDTO;
import com.finance.adam.dto.StepOneFinanceInfoDTO;
import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.repository.CorpRepository;
import com.finance.adam.repository.StockPriceRepository;
import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import com.finance.adam.repository.domain.StockPrice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScreeningService {

    private final CorpRepository corpRepository;
    private final StockPriceRepository stockPriceRepository;

    public ScreeningService(CorpRepository corpRepository, StockPriceRepository stockPriceRepository) {
        this.corpRepository = corpRepository;
        this.stockPriceRepository = stockPriceRepository;
    }

    public StepFiveStockPriceDTO getStockOpeningPrice(List<String> stockCodeList) {
        StepFiveStockPriceDTO resultDto = new StepFiveStockPriceDTO();
        Map<String, Long> openingPriceMap = new HashMap<>();
        stockCodeList.forEach(stockCode -> {
            StockPrice stockPrice = stockPriceRepository.findByCorpInfoStockCode(stockCode);
            if(stockPrice == null){
                return;
            }
            if(resultDto.getSearchTime() == null){
                resultDto.setSearchTime(stockPrice.getUpdatedAt());
            }
            openingPriceMap.put(stockCode, stockPrice.getOpeningPrice());
        });
        resultDto.setOpeningPriceMap(openingPriceMap);
        return resultDto;
    }

    public List<StepOneStockInfoDTO> getStepOneStockInfoList() {
        List<StepOneStockInfoDTO> stepOneStockInfoDTOList = new ArrayList<>();

        List<CorpInfo> corpInfoList = corpRepository.findAll();
        for (CorpInfo corpInfo : corpInfoList) {
            StepOneStockInfoDTO stockInfoDTO = new StepOneStockInfoDTO();
            stockInfoDTO.setStockName(corpInfo.getName());
            stockInfoDTO.setStockCd(corpInfo.getStockCode());

            StockPrice stockPrice = corpInfo.getStockPrice();
            stockInfoDTO.setShares(stockPrice.getListedShares());

            List<FinanceInfo> financeInfoList = corpInfo.getFinanceInfos();
            List<StepOneFinanceInfoDTO> financeInfoDTOList = new ArrayList<>();

            for (FinanceInfo financeInfo : financeInfoList) {
                if(financeInfo.getNetIncome() == null){
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

            if(financeInfoDTOList.size() < 3){
                continue;
            }

            stockInfoDTO.setFinanceInfoList(financeInfoDTOList);

            stepOneStockInfoDTOList.add(stockInfoDTO);
        }
        return stepOneStockInfoDTOList;
    }
}
