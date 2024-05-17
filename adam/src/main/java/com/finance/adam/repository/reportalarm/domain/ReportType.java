package com.finance.adam.repository.reportalarm.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

@Getter
public enum ReportType {
    A("정기공시","REGULAR"),
    B("주요사항보고","MATERIAL_FACT"),
    C("발행공시","ISSUE"),
    D("지분공시","SHAREHOLDING"),
    E("기타공시","OTHER"),
    F("외부감사관련","EXTERNAL_AUDIT"),
    G("펀드공시","FUND"),
    H("자산유동화","ASSET_LIQUIDITY"),
    I("거래소공시","EXCHANGE"),
    J("공정위공시","FAIR_TRADE");

    private String name;
    private String description;

    ReportType(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
