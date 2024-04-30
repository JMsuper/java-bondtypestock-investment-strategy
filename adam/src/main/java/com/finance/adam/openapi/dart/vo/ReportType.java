package com.finance.adam.openapi.dart.vo;

public enum ReportType {

    A("정기공시"),
    B("주요사항보고"),
    C("발행공시"),
    D("지분공시"),
    E("기타공시"),
    F("외부감사관련"),
    G("펀드공시"),
    H("자산유동화"),
    I("거래소공시"),
    J("공정위공시");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
