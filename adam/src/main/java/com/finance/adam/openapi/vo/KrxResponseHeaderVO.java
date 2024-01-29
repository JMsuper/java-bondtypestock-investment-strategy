package com.finance.adam.openapi.vo;

import lombok.Data;

/**
 * url : https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15094775
 * 금융위원회_KRX 상장종목정보 API 명세 기준
 * (24-01-29)
 */
@Data
public class KrxResponseHeaderVO {
    /**
     * API 호출 결과의 상태 코드
     */
    private String resultCode;

    /**
     * API 호출 결과의 상태
     */
    private String resultMsg;
}
