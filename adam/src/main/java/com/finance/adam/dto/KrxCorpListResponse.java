package com.finance.adam.dto;

import com.finance.adam.repository.domain.CorpInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Slf4j
public class KrxCorpListResponse {
    private LocalDateTime searchTime;

    private String stockCd;

    private String name;

    private String corpCd;

    private String market;

    private StockPriceInfoResponseDTO stockPriceInfo;

    public static KrxCorpListResponse fromCorpInfo(CorpInfo corpInfo){
            if(corpInfo.getStockPrice() == null){
                log.error("StockPrice is null");
                log.error("corpCode: {}, stockCode : {}", corpInfo.getCorpCode(), corpInfo.getStockCode());
            }
            return KrxCorpListResponse.builder()
                    .searchTime(corpInfo.getStockPrice().getUpdatedAt())
                    .corpCd(corpInfo.getCorpCode())
                    .stockCd(corpInfo.getStockCode())
                    .name(corpInfo.getName())
                    .market(corpInfo.getMarket())
                    .stockPriceInfo(StockPriceInfoResponseDTO.fromStockPrice(corpInfo.getStockPrice()))
                    .build();
    }
}
