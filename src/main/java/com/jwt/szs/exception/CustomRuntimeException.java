package com.jwt.szs.exception;

import lombok.Getter;


@Getter
public class CustomRuntimeException extends RuntimeException {

    private ErrorCode errorCode;

    private String reason;

    public CustomRuntimeException(Throwable t){
        super(t);
    }

    public CustomRuntimeException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public CustomRuntimeException(ErrorCode errorCode, String reason){
        this.errorCode = errorCode;
        this.reason = reason;
    }
}
