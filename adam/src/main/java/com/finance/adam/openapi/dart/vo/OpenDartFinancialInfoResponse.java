package com.finance.adam.openapi.dart.vo;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
public class OpenDartFinancialInfoResponse {

    /**
     * 에러 및 정보 코드
     * 정상 : "000"
     */
    private String status;

    /**
     * 에러 및 정보 메시지
     */
    private String message;

    @Override
    public String toString() {
        return status + " " + message;
    }

    private List<OpenDartFinancialInfo> list;
}
