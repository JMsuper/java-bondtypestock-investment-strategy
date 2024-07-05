package com.finance.adam.openapi.dart;

/*
    에러 메시지 참고 링크(다트 API 설명) : https://opendart.fss.or.kr/guide/detail.do?apiGrpCd=DS003&apiId=2019016
 */

public enum OpenDartResponseMsg {
    NORMAL("000", "정상"),
    UNREGISTERED_KEY("010", "등록되지 않은 키입니다."),
    UNUSABLE_KEY("011", "사용할 수 없는 키입니다. 오픈API에 등록되었으나, 일시적으로 사용 중지된 키를 통하여 검색하는 경우 발생합니다."),
    UNACCESSIBLE_IP("012", "접근할 수 없는 IP입니다."),
    NO_DATA_FOUND("013", "조회된 데이타가 없습니다."),
    FILE_NOT_FOUND("014", "파일이 존재하지 않습니다."),
    REQUEST_LIMIT_EXCEEDED("020", "요청 제한을 초과하였습니다.일반적으로는 20,000건 이상의 요청에 대하여 이 에러 메시지가 발생되나, 요청 제한이 다르게 설정된 경우에는 이에 준하여 발생됩니다."),
    COMPANY_LIMIT_EXCEEDED("021", "조회 가능한 회사 개수가 초과하였습니다.(최대 100건)"),
    INVALID_FIELD_VALUE("100", "필드의 부적절한 값입니다. 필드 설명에 없는 값을 사용한 경우에 발생하는 메시지입니다."),
    INAPPROPRIATE_ACCESS("101", "부적절한 접근입니다."),
    SERVICE_STOPPED("800", "시스템 점검으로 인한 서비스가 중지 중입니다."),
    UNDEFINED_ERROR("900", "정의되지 않은 오류가 발생하였습니다."),
    ACCOUNT_EXPIRED("901", "사용자 계정의 개인정보 보유기간이 만료되어 사용할 수 없는 키입니다. 관리자 이메일(opendart@fss.or.kr)로 문의하시기 바랍니다.");

    private final String code;
    private final String message;

    OpenDartResponseMsg(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
