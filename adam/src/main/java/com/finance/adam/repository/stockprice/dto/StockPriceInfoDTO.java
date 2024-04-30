package com.finance.adam.repository.stockprice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
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