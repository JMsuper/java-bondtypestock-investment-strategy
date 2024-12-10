package com.finance.adam.repository.stockprice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockPriceInfoDTO {
    private String stockCode;
    private String stockName;
    private String marketType;
    private String department;
    private Long closingPrice;
    private Long difference;
    private Double fluctuationRate;
    private Long openingPrice;
    private Long highPrice;
    private Long lowPrice;
    private Long volume;
    private Long tradingValue;
    private Long marketCap;
    private Long listedShares;
}