package com.finance.adam.manager;

import com.finance.adam.calculator.FinancialCalculator;
import com.finance.adam.datashuttle.KoreaFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

// 데이터 셔틀 및 계산기를 호출하여
// 배치 동작을 수행합니다.
@Component
public class BatchManager {

    @Autowired
    private KoreaFinanceService koreaFinanceService;
    @Autowired
    private FinancialCalculator financialCalculator;

    // 데이터 셔틀에 재무정보 갱신 요청
    private void updateFinanceInfo() {

    }

    private void updateAllStockPrice(){
       List<Map<String, String>> financialDataList = koreaFinanceService.getAllFinancialData();
       for(Map item : financialDataList){
           financialCalculator.expectedReturnCal(item);
       }
       // 개발중
    }


    // 사전에 정의해놓은 주식종목들의 가격을 추적하여,
    // 기대수익률이 놓아졌을 경우, 메일을 전송해주는 배치
    private void updateStockPrice(){
        List<String> stockCodeList = koreaFinanceService.getStockCodeListFromDB();
        for(String code : stockCodeList) {
            koreaFinanceService.getFinancialData(code);
        }
        // 개발중
    }



}
