package com.finance.adam.util;

public enum FnInfoName {
    CURRENT_ASSET("유동자산"),
    NON_CURRENT_ASSETS("비유동자산"),
    TOTAL_ASSET("자산총계"),
    CURRENT_LIABILITIES("유동부채"),
    NON_LIABILITIES("비유동부채"),
    TOTAL_LIABILITIES("부채총계"),
    CAPITAL("자본금"),
    RETAINED_EARNINGS("이익잉여금"),
    TOTAL_CAPITAL("자본총계"),
    REVENUE("매출액"),
    OPERATING_PROFIT("영업이익"),
    EARNINGS_BEFORE_TAX("법인세차감전 순이익"),
    NET_INCOME("당기순이익"),
    NET_LOSS("당기순이익(손실)");

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
        throw new IllegalArgumentException("No constant with value " + value);
    }

    public static FnInfoName fromName(String name) {
        return Enum.valueOf(FnInfoName.class, name);
    }
}