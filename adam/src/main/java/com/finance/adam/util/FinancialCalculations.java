package com.finance.adam.util;

import org.springframework.stereotype.Component;

@Component
public class FinancialCalculations {
    // 가상의 기업 재무 데이터를 기준으로 BPS 계산하는 메서드
    public static double calculateBPS(double EPS, double ROE) {
        return EPS/ROE;
    }
}



