package com.finance.adam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StepOneStockInfoDTO {
    private String stockName;
    private String stockCd;
    private Long shares;
    private List<StepOneFinanceInfoDTO> financeInfoList;
}
