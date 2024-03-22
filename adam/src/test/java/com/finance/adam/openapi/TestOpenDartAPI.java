package com.finance.adam.openapi;

import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.vo.OpenDartFinancialInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TestOpenDartAPI {

    @Autowired
    OpenDartAPI openDartAPI;

    @Test
    @DisplayName("Open Dart 단일기업 재무정보 조회 확인")
    void test1(){

        String corpCode = "00113261";
        String bsnsYear = "2024";

        List<OpenDartFinancialInfo> result = openDartAPI.getCorpFinancialInfo(corpCode,bsnsYear);
        assertNotNull(result);
    }

    @Test
    @DisplayName("Open Dart 공시 기업코드 조회 확인(삼성전자 종목코드 활용)")
    void test2(){
        Map<String, String> map = openDartAPI.getCorpCodeMap();

        String SAMSUNG_CORP_CODE = "00126380";
        String SAMSUNG_STOCK_CODE = "005930";

        assertTrue(map.size() > 3000);
        assertEquals(SAMSUNG_CORP_CODE,map.get(SAMSUNG_STOCK_CODE));
    }
}
