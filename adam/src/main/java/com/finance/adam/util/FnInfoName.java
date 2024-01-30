package com.finance.adam.util;

import lombok.Getter;

@Getter
public final class FnInfoName {
    /**
     * 유동자산
     */
    static final String CURRENT_ASSET = "CURRENT_ASSET";
    /**
     * 비유동자산
     */
    static final String NON_CURRENT_ASSETS = "ON_CURRENT_ASSETS";
    /**
     * 자산총계
     */
    static final String TOTAL_ASSET = "TOTAL_ASSET";
    /**
     * 유동부채
     */
    static final String CURRENT_LIABILITIES = "CURRENT_LIABILITIES";
    /**
     * 비유동부채
     */
    static final String NON_LIABILITIES = "NON_LIABILITIES";
    /**
     * 부채총계
     */
    static final String TOTAL_LIABILITIES = "TOTAL_LIABILITIES";
    /**
     * 자본금
     */
    static final String CAPITAL = "CAPITAL";
    /**
     * 이익잉여금
     */
    static final String RETAINED_EARNINGS = "RETAINED_EARNINGS";
    /**
     * 자본총계
     */
    static final String TOTAL_CAPITAL = "TOTAL_CAPITAL";
    /**
     * 매출액
     */
    static final String REVENUE = "REVENUE";
    /**
     * 영업이익
     */
    static final String OPERATING_PROFIT = "OPERATING_PROFIT";
    /**
     * 법인세차감전 순이익
     */
    static final String EARNINGS_BEFORE_TAX = "EARNINGS_BEFORE_TAX";
    /**
     * 당기순이익
     */
    static final String NET_INCOME = "NET_INCOME";
    /**
     * 당기순이익(손실)<br>
     * ※ 아직은 당기순이익과 차이를 모르겠음
     */
    static final String NET_LOSS = "NET_LOSS";
}
