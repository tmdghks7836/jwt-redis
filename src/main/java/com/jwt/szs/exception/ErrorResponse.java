package com.jwt.szs.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;


@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String timestamp;
    private String error;
    private String code;
    private String message;
    private String reason;

    public static ErrorResponse getByErrorCode(ErrorCode errorCode) {

        return getByErrorCode(errorCode, null);
    }

    public static ErrorResponse getByErrorCode(ErrorCode errorCode, String reason) {

        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
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