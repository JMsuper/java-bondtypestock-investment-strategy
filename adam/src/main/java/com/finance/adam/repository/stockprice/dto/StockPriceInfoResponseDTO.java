package com.finance.adam.repository.stockprice.dto;

import com.finance.adam.repository.stockprice.domain.StockPrice;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class StockPriceInfoResponseDTO {
    private Long closingPrice;
    private Long difference;
    private Double fluctuationRate;
    private Long openingPrice;

    public static StockPriceInfoResponseDTO fromStockPrice(StockPrice stockPrice){
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
}