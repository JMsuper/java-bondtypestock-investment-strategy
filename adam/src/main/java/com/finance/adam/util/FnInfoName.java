package com.finance.adam.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum FnInfoName {
    currentAsset("유동자산"),
    nonCurrentAsset("비유동자산"),
    totalAsset("자산총계"),
    currentLiabilities("유동부채"),
    nonLiabilities("비유동부채"),
    totalLiabilities("부채총계"),
    capital("자본금"),
    retainedEarnings("이익잉여금"),
    totalCapital("자본총계"),
    revenue("매출액"),
    operatingProfit("영업이익"),
    earningsBeforeTax("법인세차감전 순이익"),
    netIncome("당기순이익"),
    netLoss("당기순이익(손실)"),
    comprehensiveIncome("총포괄손익");

    private String value;

    FnInfoName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static FnInfoName fromValue(String value) {
        for (FnInfoName fn : values()) {
            if (fn.getValue().equals(value)) {
                return fn;
            }
        }
        log.info("No constant with value " + value);
        return null;
    }

    public static FnInfoName fromName(String name) {
        return Enum.valueOf(FnInfoName.class, name);
    }
}