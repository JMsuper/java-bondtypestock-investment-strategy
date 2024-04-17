package com.finance.adam.service;

import com.finance.adam.dto.KrxCorpListResponse;
import com.finance.adam.util.FnInfoName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TestFinanceDataService {

    @Autowired
    FinanceDataService financeDataService;

    @Test
    @DisplayName("기업 정보 가져오기")
    void test1(){
        List<KrxCorpListResponse> krxCorpListResponses = financeDataService.getKrxCorpInfo();
        assertThat(krxCorpListResponses).isNotNull();
    }

    @Test
    @DisplayName("재무 정보 가져와서 DB에 저장하기")
    void test2(){
        financeDataService.renewFinancialInfo();
    }

    @Test
    @DisplayName("renewKrxStockList")
    void renewFinanceStockList(){
        financeDataService.renewCorpInfoWithKrxList();
    }
}
