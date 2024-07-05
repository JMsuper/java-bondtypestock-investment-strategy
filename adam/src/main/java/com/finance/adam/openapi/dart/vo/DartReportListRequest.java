package com.finance.adam.openapi.dart.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.finance.adam.repository.reportalarm.domain.ReportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DartReportListRequest{

    /**
     * 발급받은 인증키(40자리)
     */
    private final String crtfcKey;

    /**
     * 공시대상회사의 고유번호(8자리)<br>
     * !거래서 종목코드가 아님!
     */
    private final String corpCode;

    /**
     * 검색시작 접수일자(YYYYMMDD)
     * 1) 기본값 : 종료일(end_de)
     * 2) 고유번호(corp_code)가 없는 경우 검색기간은 3개월로 제한
     */
    private final String bgnDe;

    private final ReportType pblntfTy;

    /**
     * 페이지 별 건수
     * 페이지당 건수(1~100) 기본값 : 10, 최대값 : 100
     */
    private final String pageCount;

}
