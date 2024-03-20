package com.finance.adam.dto;

import com.finance.adam.repository.domain.CorpInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class KrxCorpListResponse {
    private String stockCd;

    private String name;

    private String corpCd;

    private String market;

    public static KrxCorpListResponse fromCorpInfo(CorpInfo corpInfo){
            return KrxCorpListResponse.builder()
                    .corpCd(corpInfo.getCorpCode())
                    .stockCd(corpInfo.getStockCode())
                    .name(corpInfo.getName())
                    .market(corpInfo.getMarket())
                    .build();
    }
}
