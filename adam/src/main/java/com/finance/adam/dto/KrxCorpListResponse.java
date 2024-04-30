package com.finance.adam.dto;

import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.stockprice.dto.StockPriceInfoResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class KrxCorpListResponse {
    private LocalDateTime searchTime;

    private String stockCd;

    private String name;

    private String corpCd;

    private String market;

    private StockPriceInfoResponseDTO stockPriceInfo;

    public static KrxCorpListResponse fromCorpInfo(CorpInfo corpInfo){
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
