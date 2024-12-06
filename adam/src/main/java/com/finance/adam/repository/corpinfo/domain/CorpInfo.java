package com.finance.adam.repository.corpinfo.domain;


import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import com.finance.adam.repository.financeinfo.domain.FinanceInfo;
import com.finance.adam.repository.stockprice.domain.StockPrice;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorpInfo {

    @Id
    @Setter
    private String corpCode;

    private String name;

    @Setter
    private String stockCode;

    private String market;

    private String baseDt;

    /**
     * 상장폐지 여부
     */
    @Setter
    @Builder.Default
    private boolean deListed = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "corpInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<FinanceInfo> financeInfos;

    @OneToOne(mappedBy = "corpInfo", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private StockPrice stockPrice;


    public String getParsedStockCode(){
        return this.stockCode.substring(1);
    }

    public static CorpInfo fromKrxItemInfo(KrxItemInfo krxItemInfo){
        return CorpInfo.builder()
                .stockCode(krxItemInfo.getSrtnCd())
                .name(krxItemInfo.getCorpNm())
                .market(krxItemInfo.getMrktCtg())
                .baseDt(krxItemInfo.getBasDt())
                .build();
    }

}
