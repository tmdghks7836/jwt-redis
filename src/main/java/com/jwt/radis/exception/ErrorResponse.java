package com.jwt.radis.exception;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;


@Getter
@SuperBuilder
public class ErrorResponse {
    private final String timestamp = LocalDateTime.now().toString();
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ErrorResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .message(errorCode.getDescription())
                        .build()
                );
    }
}