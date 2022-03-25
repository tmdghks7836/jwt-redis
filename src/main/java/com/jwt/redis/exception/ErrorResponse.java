package com.jwt.redis.exception;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;


@Getter
@ToString
@SuperBuilder
public class ErrorResponse {
    private final String timestamp = LocalDateTime.now().toString();
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String reason;

    public static ErrorResponse getByErrorCode(ErrorCode errorCode) {

        return getByErrorCode(errorCode, null);
    }

    public static ErrorResponse getByErrorCode(ErrorCode errorCode, String reason) {

        return ErrorResponse.builder()
                .status(errorCode.getHttpStatus().value())
                .error(errorCode.getHttpStatus().name())
                .code(errorCode.getCode())
                .message(errorCode.getDescription())
                .reason(reason)
                .build();
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return toResponseEntity(errorCode, null);
    }

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode, String reason) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(getByErrorCode(errorCode, reason)
                );
    }
}