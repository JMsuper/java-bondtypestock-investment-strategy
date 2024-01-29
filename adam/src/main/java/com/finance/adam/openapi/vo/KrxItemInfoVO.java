package com.finance.adam.openapi.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * url : https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15094775
 * 금융위원회_KRX 상장종목정보 API 명세 기준
 * (24-01-29)
 */
@Data
public class KrxItemInfoVO {

    /**
     * YYYYMMDD 조회의 기준일, 통상 거래일
     */
    private String basDt;

    /**
     * 종목 코드보다 짧으면서 유일성이 보장되는 코드
     */
    private String srtnCd;

    /**
     * 국제 채권 식별 번호. 유가증권(채권)의 국제인증 고유번호
     */
    private String isinCd;

    /**
     * 시장 구분 (KOSPI/KOSDAQ/KONEX 등)
     */
    private String mrktCtg;

    /**
     * 유가증권 국제인증 고유번호 코드 이름
     */
    private String itmsNm;

    /**
     * 종목의 법인등록번호
     */
    private String crno;

    /**
     * 종목의 법인 명칭
     */
    private String corpNm;
}
