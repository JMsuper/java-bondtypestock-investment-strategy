package com.finance.adam.openapi.dart.vo;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OpenDartFinancialInfoRequest {

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
     * 사업연도(4자리) ※ 2015년 이후 부터 정보제공
     */
    private final String bsnsYear;

    /**
     * - 1분기보고서 : 11013<br>
     * - 반기보고서 : 11012<br>
     * - 3분기보고서 : 11014<br>
     * - 사업보고서 : 11011
     */
    private final String reprtCode;

}
