package com.finance.adam.exception;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseEntity> handleServerException(Exception e){
        return ErrorResponseEntity.toResponseEntity(ErrorCode.SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponseEntity> handleNoResourceFoundException(NoResourceFoundException e){
        return ErrorResponseEntity.toResponseEntity(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        HashMap<String,String> errMessage = new HashMap<>();

        for (FieldError error : result.getFieldErrors()) {
            errMessage.put(error.getField(), error.getDefaultMessage());
        }

        return new ResponseEntity<>(errMessage , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity handleMethodNotAllowedException() {
        return ErrorResponseEntity.toResponseEntity(ErrorCode.NOT_ALLOWED_METHOD);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException() {
        return ErrorResponseEntity.toResponseEntity(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity handleJsonParseException() {
        return ErrorResponseEntity.toResponseEntity(ErrorCode.JSON_PARSE_ERROR);
    }
}