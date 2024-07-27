package com.finance.adam.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.OpenDartUtil;
import com.finance.adam.openapi.dart.vo.DartFinancialInfo;
import com.finance.adam.openapi.dart.vo.DartReportDTO;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {
        OpenDartAPI.class, OpenDartUtil.class,
        RestTemplate.class, ObjectMapper.class
})
public class TestOpenDartAPI {

    @Autowired
    OpenDartAPI openDartAPI;

    @Test
    @DisplayName("Open Dart 단일기업 재무정보 조회 확인")
    void test1(){

        String corpCode = "00113261";
        String bsnsYear = "2021";

        List<DartFinancialInfo> result = openDartAPI.getCorpFinancialInfo(corpCode,bsnsYear);
        assertTrue(result != null);
        assertTrue(result.size() > 0);
        assertTrue(result.get(0).getCorpCode().equals(corpCode));
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

    @Test
    @DisplayName("Open Dart 공시 보고서 조회 - 보고서유형 미지정")
    void test3(){
        String corpCode = "00126380";
        int pageCount = 5;

        List<DartReportDTO> result = openDartAPI.getRecentReportList(corpCode,pageCount);
        assertNotNull(result);
        assertTrue(result.size() == 5);
        for(DartReportDTO dto : result){
            assertTrue(dto.getCorpCls().equals("Y"));
            assertTrue(dto.getCorpName().equals("삼성전자"));
        }
    }

    @Test
    @DisplayName("Open Dart 공시 보고서 조회 - 보고서유형 지정")
    void test4(){
        String corpCode = "00126380";
        int pageCount = 5;

        List<DartReportDTO> result = openDartAPI.getRecentReportList(corpCode,pageCount, ReportType.A);
        assertNotNull(result);
        assertTrue(result.size() == 5);

        for(DartReportDTO dto : result){
            assertTrue(dto.getReportNm().contains("보고서"));
        }
    }
}
