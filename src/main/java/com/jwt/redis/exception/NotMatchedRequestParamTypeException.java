package com.jwt.redis.exception;

import lombok.Getter;


@Getter
public class NotMatchedRequestParamTypeException extends CustomRuntimeException {

    public NotMatchedRequestParamTypeException() {
        super(ErrorCode.NOT_MATCHED_PARAM_TYPE);
    }

    public NotMatchedRequestParamTypeException(String reason) {
        super(ErrorCode.NOT_MATCHED_PARAM_TYPE, reason);
    }
}
