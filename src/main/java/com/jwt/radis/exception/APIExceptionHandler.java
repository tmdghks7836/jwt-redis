package com.jwt.radis.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public void handleSomeException(RuntimeException ex, WebRequest request) throws Exception {

        ex.printStackTrace();
        // SomeException 예외 발생시 처리 로직 작성
        handleException(ex, request);
    }

    @ExceptionHandler(value = {LRuntimeException.class})
    protected ResponseEntity<ErrorResponse> handleDataException(LRuntimeException e) {
        e.printStackTrace();
        log.error("handleDataException throw Exception : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }
}