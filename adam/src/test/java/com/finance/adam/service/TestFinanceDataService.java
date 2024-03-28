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
    @DisplayName("재무 정보 가져와서 DB에 저장하기")
    void test2(){
        financeDataService.renewFinancialInfo();
    }
}
