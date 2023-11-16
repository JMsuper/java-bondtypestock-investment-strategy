package com.finance.adam.calculator;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FinancialCalculator {
    public double expectedReturnCal(Map financeInfo){return 0;}

    // ROE = (EPS/BPS) * 100
    static public double calculateBPS(double EPS, double ROE){
        return (EPS*100)/ROE;
    }

    // 기대 수익률 계산
    // 1단계 : 현재가치 확인(주당 순 자산가치)
    // 2단계 : 미래수익률 예측(ROE)
    // 3단계 : 10년 후 주당순자산가치 = 현재 주당순자산가치 * (1 + 예상 ROE)^10

    static public double calculateExpectedReturn(double BPS, double ROE, double price){
        // 기대 수익률 계산
        // 1단계 : 현재가치 확인(주당 순 자산가치)
        // 2단계 : 미래수익률 예측(ROE)
        // 3단계 : 10년 후 주당순자산가치 = 현재 주당순자산가치 * (1 + 예상 ROE)^10
        double BPSAfterTenYears = BPS * Math.pow (1 + ROE/100,10);
//        System.out.println("10년 후 주당순자산가치 : " + Double.toString(BPSAfterTenYears));
        // 4단계 : 예상 순자산 가치를 현재 가격으로 나눈 값이 10년의 몇 퍼센트 승수인지 계산
        double temp = BPSAfterTenYears / price;
        double expectedReturn = Math.pow(temp,0.1) - 1;
//        System.out.println("기대 수익률 : " + Double.toString(expectedReturn));
        return expectedReturn;
    }

}



