package com.jwt.redis.exception.handler;

import com.jwt.redis.exception.CustomRuntimeException;
import com.jwt.redis.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
public class APIExceptionHandler {


    @ExceptionHandler(Exception.class)
    public ResponseEntity handleSomeException(Exception e, WebRequest request) throws Exception {

        e.printStackTrace();
        // SomeException 예외 발생시 처리 로직 작성
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponse.builder()
                        .status(httpStatus.value())
                        .error(httpStatus.name())
                        .build()
                );
    }

    @ExceptionHandler(value = {CustomRuntimeException.class})
    protected ResponseEntity<ErrorResponse> handleDataException(CustomRuntimeException e) {
        e.printStackTrace();
        log.error("handleDataException throw Exception : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode(), e.getReason());
    }

    @ExceptionHandler(value = BindException.class)
    public ResponseEntity handleMethodArgumentNotValid(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]  ");
        }

        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        return ResponseEntity
                .status(badRequest)
                .body(ErrorResponse.builder()
                        .status(badRequest.value())
                        .error(badRequest.name())
                        .message(builder.toString())
                        .build()
                );
    }
}