package com.finance.adam.util;

import org.springframework.stereotype.Component;

@Component
public class FinanceCalculator {

    /*
     * BPS(BookValue Per Share) 계산
     * BPS = 자본총계 / 상장주식수
     */
    public long calculateBPS(long totalCapital, long listedShares){
        return totalCapital / listedShares;
    }

    /*
     * EPS(Earning Per Share) 계산
     * EPS = 당기순이익 / 상장주식수
     */
    public long calculateEPS(long netIncome, long listedShares) {
        return netIncome / listedShares;
    }

    /*
     * ROE(Return On Equity) 계산
     * ROE = 금년도 순이익 / 전년도 순자산
     */
    public float calculateROE(long thisYearNetIncome, long lastYearTotalCapital) {
        return (float) thisYearNetIncome / lastYearTotalCapital;
    }

    /*
     * 예상 10년 후 BPS 계산
     * 예상 10년 후 BPS = 금년도 BPS * (1 + 예상 ROE) ^ 10
     */
    public long calculateAfterTenYearBPS(
        long bps,
        float afterTenYearsAverageROE
    ){
        return (long) (bps * Math.pow(1 + afterTenYearsAverageROE, 10));
    }

    public float calculateExpectedRate(
            long openingPrice,
            float afterTenYearsBPS
    ){
        float expectedRate = (float) Math.pow((double) afterTenYearsBPS / openingPrice, 0.1) - 1;
        return expectedRate;
    }

    public int calculateTargetPrice(
            float targetRate,
            long afterTenYearsBPS
    ){
        return (int)(afterTenYearsBPS / Math.pow((1 + targetRate), 10));
    }

}
