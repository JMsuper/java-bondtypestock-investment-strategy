package com.finance.adam.repository.stockprice.dto;

import com.finance.adam.repository.stockprice.domain.StockPrice;
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

    public static StockPriceInfoDTO from(StockPrice entity){
        return StockPriceInfoDTO.builder()
                .stockCode(entity.getCorpInfo().getParsedStockCode())
                .stockName(entity.getCorpInfo().getName())
                .marketType(entity.getCorpInfo().getMarket())
//                .department() StockPrice 에서는 소속부(department)를 관리하지 않음
                .closingPrice(entity.getClosingPrice())
                .difference(entity.getDifference())
                .fluctuationRate(entity.getFluctuationRate())
                .openingPrice(entity.getOpeningPrice())
                .highPrice(entity.getHighPrice())
                .lowPrice(entity.getLowPrice())
                .volume(entity.getVolume())
                .tradingValue(entity.getTradingValue())
                .marketCap(entity.getMarketCap())
                .listedShares(entity.getListedShares())
                .build();
    }
}