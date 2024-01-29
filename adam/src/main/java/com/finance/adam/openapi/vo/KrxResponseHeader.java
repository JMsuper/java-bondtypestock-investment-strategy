package com.finance.adam.openapi.vo;

import lombok.Getter;
import lombok.ToString;

/**
 * * 공공데이터포털 API <br>
 * - 금융위원회_KRX 상장종목정보<br>
 * - (24-01-29)
 */

@Getter
@ToString
public class KrxResponseHeader {
    /**
     * API 호출 결과의 상태 코드
     */
    private String resultCode;

    /**
     * API 호출 결과의 상태
     */
    private String resultMsg;
}
