package com.jwt.radis.exception;

import lombok.Getter;


@Getter
public class LRuntimeException extends RuntimeException {

    private ErrorCode errorCode;

    private String reason;

    public LRuntimeException(Throwable t){
        super(t);
    }

    public LRuntimeException(ErrorCode errorCode){
        this.errorCode = errorCode;
    }

    public LRuntimeException(ErrorCode errorCode, String reason){
        this.errorCode = errorCode;
        this.reason = reason;
    }
}
