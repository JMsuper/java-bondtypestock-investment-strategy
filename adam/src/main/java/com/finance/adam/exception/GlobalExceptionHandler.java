package com.finance.adam.exception;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseEntity> handleServerException(Exception e){
        log.error("error occur : ", e);
        return ErrorResponseEntity.toResponseEntity(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity handleJsonParseException() {
        return ErrorResponseEntity.toResponseEntity(ErrorCode.JSON_PARSE_ERROR);
    }
}