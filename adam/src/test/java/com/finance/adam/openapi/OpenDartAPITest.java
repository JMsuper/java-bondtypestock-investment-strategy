package com.finance.adam.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.adam.openapi.dart.OpenDartAPI;
import com.finance.adam.openapi.dart.OpenDartUtil;
import com.finance.adam.openapi.dart.dto.DartFinancialInfo;
import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.repository.corpinfo.CorpRepository;
import com.finance.adam.repository.corpinfo.domain.CorpInfo;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import com.finance.adam.service.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
public class OpenDartAPITest {

    @Autowired
    RedisService redisService;
    @Autowired
    OpenDartAPI openDartAPI;
    @Autowired
    CorpRepository corpRepository;

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

        List<DartReportDTO> result = openDartAPI.getRecentReportListFive(corpCode);
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

    @Test
    @DisplayName("공시 보고서 5건 조회 Redis 캐싱 - 성공(5건 있는 경우")
    void test5(){
        // given
        String testCorpCode = "TEST123456";
        int reportCnt = 5;

        // 특정 종목코드에 해당하는 key 삭제
        redisService.deleteKey(testCorpCode);
        // 특정 종목코드에 대해 공시 5건 추가
        for(int i = 1; i <= reportCnt; i++){
            DartReportDTO dto = new DartReportDTO(
                    "Y","종목명",testCorpCode,testCorpCode,
                    "","","제출인명",
                    "2024110" + i,"유"
            );
            redisService.pushToList(testCorpCode,dto);
        }

        // when
        // 종목코드에 해당하는 공시 조회
        List<DartReportDTO> list = openDartAPI.getRecentReportListFive(testCorpCode);

        // then
        // 반환된 리스트 개수는 5개
        assertTrue(list.size() == reportCnt);
        list.forEach( item -> assertTrue(item.getCorpCode().equals(testCorpCode)));
    }

    @Test
    @DisplayName("전체 종목 최근공시 5건 레디스 PUSH 기능 테스트 - 성공")
    void test6(){
        // given
        // 전체 종목 조회
        List<CorpInfo> corpInfoList = corpRepository.findAll();
        String samsungCorpCd = "00126380";
        // 전체 종목에 대한 레디스 內 key 삭제
        List<String> corpCodeList = corpInfoList.stream().map((CorpInfo::getCorpCode)).toList();
        for(String corpCd : corpCodeList){
            redisService.deleteKey(corpCd);
            assertTrue(!redisService.isKeyExists(corpCd));
        }

        // when
        // 레디스 갱신
        int resultCnt = openDartAPI.initRecentReportInRedis(corpCodeList);

        // then
        assertTrue(resultCnt == corpInfoList.size());
        for(String corpCd : corpCodeList){
            List<DartReportDTO> dtoList = openDartAPI.getRecentReportListFive(corpCd);
            assertTrue(!dtoList.isEmpty());
            for(DartReportDTO dto : dtoList){
                assertTrue(!dto.getCorpCode().isEmpty());
                assertTrue(!dto.getReportNm().isEmpty());
            }
        }
    }

    @Test
    @DisplayName("신규 공시에 대해 레디스 갱신 - 성공")
    void test7(){
        openDartAPI.updateRecentReportInRedis();
        // given

        // when

        // then
    }
}
