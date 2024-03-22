package com.finance.adam.service;

import com.finance.adam.util.FnInfoName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class TestFinanceDataService {

    @Autowired
    FinanceDataService financeDataService;

    @Test
    @DisplayName("기업 주요 재무정보 가져오기")
    void test(){
        String corpCode = "00126380";

        String[] yearList = {"2020","2021","2022","2023"};

        for(int i = 0; i < yearList.length; i++){
            String bsnsYear = yearList[i];

            Map<String, Long> result = financeDataService.getFinancialInfo(corpCode,bsnsYear);
            System.out.println(result);

            Long totalCapital = result.get(FnInfoName.TOTAL_CAPITAL);
            Long netIncome = result.get(FnInfoName.NET_INCOME);
            Long netLoss = result.get(FnInfoName.NET_LOSS);
            float ROE = (float)netIncome / totalCapital;
            System.out.println(bsnsYear);
            System.out.println(ROE);
//            System.out.println((float) netLoss/totalCapital);
        }
    }

    @Test
    @DisplayName("재무 정보 가져와서 DB에 저장하기")
    void test2(){
        financeDataService.renewFinancialInfo();
    }
}
