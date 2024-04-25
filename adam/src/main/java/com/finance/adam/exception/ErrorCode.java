package com.finance.adam.exception;

import lombok.Getter;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SAVE_CORP_MAX_COUNT(HttpStatus.BAD_REQUEST, "SAVE_CORP_MAX", "종목 저장 최대 개수(10개)를 초과하였습니다."),
    SAVE_CORP_INFO_DUPLICATED(HttpStatus.BAD_REQUEST, "SAVE_CORP_INFO_DUPLICATED", "이미 저장된 종목입니다."),
    SAVE_CORP_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "SAVE_CORP_INFO_NOT_FOUND", "저장된 종목을 찾을 수 없습니다."),

    CORP_NOT_FOUND(HttpStatus.NOT_FOUND, "CORP_NOT_FOUND", "종목을 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "잘못된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "NOT_FOUND", "찾을 수 없습니다."),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "ILLEGAL_ARGUMENT", "잘못된 인자입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증되지 않은 요청입니다."),

    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "JSON_PARSE_ERROR", "잘못된 JSON 형식입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}