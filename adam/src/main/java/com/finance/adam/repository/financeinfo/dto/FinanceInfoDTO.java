package com.finance.adam.repository.financeinfo.dto;

import com.finance.adam.repository.financeinfo.domain.FinanceInfo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class FinanceInfoDTO implements Serializable {
    private int year;
    private String fsDiv;
    private Long currentAsset;
    private Long nonCurrentAsset;
    private Long totalAsset;
    private Long currentLiabilities;
    private Long nonLiabilities;
    private Long totalLiabilities;
    private Long capital;
    private Long retainedEarnings;
    private Long totalCapital;
    private Long revenue;
    private Long operatingProfit;
    private Long earningsBeforeTax;
    private Long netIncome;
    private Long netLoss;

    public static FinanceInfoDTO fromFinanceInfo(FinanceInfo financeInfo){
        FinanceInfoDTO financeInfoDTO = new FinanceInfoDTO();
        financeInfoDTO.setYear(financeInfo.getYear());
        financeInfoDTO.setFsDiv(financeInfo.getFsDiv());
        financeInfoDTO.setCurrentAsset(financeInfo.getCurrentAsset());
        financeInfoDTO.setNonCurrentAsset(financeInfo.getNonCurrentAsset());
        financeInfoDTO.setTotalAsset(financeInfo.getTotalAsset());
        financeInfoDTO.setCurrentLiabilities(financeInfo.getCurrentLiabilities());
        financeInfoDTO.setNonLiabilities(financeInfo.getNonLiabilities());
        financeInfoDTO.setTotalLiabilities(financeInfo.getTotalLiabilities());
        financeInfoDTO.setCapital(financeInfo.getCapital());
        financeInfoDTO.setRetainedEarnings(financeInfo.getRetainedEarnings());
        financeInfoDTO.setTotalCapital(financeInfo.getTotalCapital());
        financeInfoDTO.setRevenue(financeInfo.getRevenue());
        financeInfoDTO.setOperatingProfit(financeInfo.getOperatingProfit());
        financeInfoDTO.setEarningsBeforeTax(financeInfo.getEarningsBeforeTax());
        financeInfoDTO.setNetIncome(financeInfo.getNetIncome());
        financeInfoDTO.setNetLoss(financeInfo.getNetLoss());
        return financeInfoDTO;
    }
}