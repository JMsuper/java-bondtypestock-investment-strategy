package com.finance.adam.openapi.dart.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class OpenDartBaseResponseDTO {
    /**
     * 에러 및 정보 코드
     * 정상 : "000"
     */
    @Setter
    private String status;

    /**
     * 에러 및 정보 메시지
     */
    private String message;

    @Override
    public String toString() {
        return status + " " + message;
    }

    private List<Object> list;
}
