package com.finance.adam.openapi.dart.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DartFinancialInfo {

    /**
     * 접수번호(14자리)<br>
     * ※ 공시뷰어 연결에 이용예시<br>
     * - PC용 : https://dart.fss.or.kr/dsaf001/main.do?rcpNo=접수번호
     */
    private String rceptNo;

    /**
     * 사업 연도
     */
    private String bsnsYear;

    /**
     * 상장회사의 종목코드(6자리)
     */
    private String stockCode;

    /**
     * 보고서 코드
     */
    private String reprtCode;

    /**
     * 계정명
     */
    private String accountNm;

    /**
     * 개별/연결구분
     * OFS:재무제표, CFS:연결재무제표
     */
    private String fsDiv;

    /**
     * 개별/연결명
     * ex) 연결재무제표 또는 재무제표 출력
     */
    private String fsNm;

    /**
     * 재무제표구분
     * BS:재무상태표, IS:손익계산서
     */
    private String sjDiv;

    /**
     * 재무제표명
     * ex) 재무상태표 또는 손익계산서 출력
     */
    private String sjNm;

    /**
     * 당기명
     * ex) 제 13 기 3분기말
     */
    private String thstrmNm;

    /**
     * 당기일자
     * ex) 2018.09.30 현재
     */
    private String thstrmDt;

    /**
     * 당기금액
     * ex) 9,999,999,999
     */
    private String thstrmAmount;

    /**
     * 당기누적금액
     * ex) 9,999,999,999
     */
    private String thstrmAddAmount;

    /**
     * 전기명
     * ex) 제 12 기말
     */
    private String frmtrmNm;

    /**
     * 전기일자
     * ex) 2017.01.01 ~ 2017.12.31
     */
    private String frmtrmDt;

    /**
     * 전기금액
     * ex) 9,999,999,999
     */
    private String frmtrmAmount;

    /**
     * 전기누적금액
     * ex) 9,999,999,999
     */
    private String frmtrmAddAmount;

    /**
     * 전전기명
     * ex) 제 11 기말(※ 사업보고서의 경우에만 출력)
     */
    private String bfefrmtrmNm;

    /**
     * 전전기일자
     * ex) 2016.12.31 현재(※ 사업보고서의 경우에만 출력)
     */
    private String bfefrmtrmDt;

    /**
     * 전전기금액
     * ex) 9,999,999,999(※ 사업보고서의 경우에만 출력)
     */
    private String bfefrmtrmAmount;

    /**
     * 계정과목 정렬순서
     */
    private String ord;

    /**
     * 통화 단위
     */
    private String currency;

}
