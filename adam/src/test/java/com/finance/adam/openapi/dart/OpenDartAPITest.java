package com.finance.adam.openapi.dart;

import static org.junit.jupiter.api.Assertions.*;

import com.finance.adam.openapi.dart.dto.DartReportDTO;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
@ActiveProfiles("api") // application-test.yml 설정 사용
class OpenDartAPITest {

    @Autowired
    private OpenDartAPI openDartAPI;

    @Test
    @DisplayName("updateRecentReportInRedis 실제 API 호출 테스트")
    void updateRecentReportInRedisTest() {
        // When
        HashMap<ReportType, List<DartReportDTO>> result = openDartAPI.updateRecentReportInRedis();

        // Then
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(ReportType.values().length, result.size()),
            () -> {
                // 각 ReportType별 결과 검증
                result.forEach((reportType, reports) -> {
                    System.out.println(String.format("ReportType: %s, Count: %d", 
                        reportType, reports.size()));
                    
                    // 각 보고서의 필수 필드 검증
                    reports.forEach(report -> {
                        assertNotNull(report.getCorpCode(), "Corp code should not be null");
                        assertNotNull(report.getRceptNo(), "Receipt number should not be null");
                        assertNotNull(report.getRceptDt(), "Receipt date should not be null");
                        assertNotNull(report.getReportNm(), "Report name should not be null");
                    });
                });
            }
        );
    }

    @Test
    @DisplayName("특정 ReportType에 대한 상세 테스트")
    void getRecentReportListForSpecificTypeTest() {
        // Given
        ReportType targetType = ReportType.A; // 사업보고서

        // When
        CompletableFuture<List<DartReportDTO>> futureResult =
            openDartAPI.getRecentReportList(null, 100, targetType);
        List<DartReportDTO> reports = futureResult.join();

        // Then
        assertAll(
            () -> assertNotNull(reports),
            () -> assertFalse(reports.isEmpty(), "Should return at least one report"),
            () -> {
                DartReportDTO firstReport = reports.get(0);
                System.out.println("Sample Report:");
                System.out.println("- Corp Code: " + firstReport.getCorpCode());
                System.out.println("- Report Name: " + firstReport.getReportNm());
                System.out.println("- Receipt Date: " + firstReport.getRceptDt());
                System.out.println("- Receipt No: " + firstReport.getRceptNo());
            }
        );
    }

    @Test
    @DisplayName("API 호출 시간 성능 테스트")
    void apiPerformanceTest() {
        // Given
        long startTime = System.currentTimeMillis();

        // When
        HashMap<ReportType, List<DartReportDTO>> result = 
            openDartAPI.updateRecentReportInRedis();

        // Then
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("API 호출 총 소요시간: " + duration + "ms");
        
        // 일반적으로 10초 이내에 완료되어야 함
        assertTrue(duration < 10000, 
            "API call should complete within 10 seconds but took " + duration + "ms");
    }
}