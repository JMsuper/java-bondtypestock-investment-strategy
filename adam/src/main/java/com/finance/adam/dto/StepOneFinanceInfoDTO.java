package com.finance.adam.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StepOneFinanceInfoDTO {
    private Integer year;
    private String fsDiv;
    private Long totalAsset;
    private Long totalLiabilities;
    private Long totalCapital;
    private Long netIncome;
}
