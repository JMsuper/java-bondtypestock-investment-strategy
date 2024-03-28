package com.finance.adam.repository.domain;


import com.finance.adam.openapi.publicdataportal.vo.KrxItemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorpInfo {

    @Id
    private String corpCode;

    private String name;

    private String stockCode;

    private String market;

    private String baseDt;

    @OneToMany(mappedBy = "corpInfo")
    private List<FinanceInfo> financeInfos;

    @OneToOne(mappedBy = "corpInfo")
    private StockPrice stockPrice;


    public String getParsedStockCode(){
        return this.stockCode.substring(1);
    }

    public void setCorpCode(String corpCode) {
        this.corpCode = corpCode;
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
