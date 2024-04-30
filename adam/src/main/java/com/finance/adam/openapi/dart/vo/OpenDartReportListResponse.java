package com.finance.adam.openapi.dart.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

import java.util.List;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OpenDartReportListResponse {

    /**
     * 에러 및 정보 코드
     * 정상 : "000"
     * 조회 데이터 없음 : "013"
     */
    private String status;

    /**
     * 에러 및 정보 메시지
     */
    private String message;

    /**
     * 페이지 번호
     */
    private int pageNo;

    /**
     * 페이지 별 건수
     */
    private int pageCount;

    /**
     * 총 건수
     */
    private int totalCount;

    /**
     * 총 페이지 수
     */
    private int totalPage;

    private List<OpenDartReportDTO> list;
}
