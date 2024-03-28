package com.finance.adam.service;

import com.finance.adam.dto.StepOneFinanceInfoDTO;
import com.finance.adam.dto.StepOneStockInfoDTO;
import com.finance.adam.repository.CorpRepository;
import com.finance.adam.repository.domain.CorpInfo;
import com.finance.adam.repository.domain.FinanceInfo;
import com.finance.adam.repository.domain.StockPrice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScreeningService {

    private final CorpRepository corpRepository;

    public ScreeningService(CorpRepository corpRepository) {
        this.corpRepository = corpRepository;
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
            if(financeInfoList.size() < 3){
                continue;
            }
            List<StepOneFinanceInfoDTO> financeInfoDTOList = new ArrayList<>();

            for (FinanceInfo financeInfo : financeInfoList) {
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

            stockInfoDTO.setFinanceInfoList(financeInfoDTOList);

            stepOneStockInfoDTOList.add(stockInfoDTO);
        }
        return stepOneStockInfoDTOList;
    }
}
