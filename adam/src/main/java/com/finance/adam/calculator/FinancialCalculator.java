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

}



