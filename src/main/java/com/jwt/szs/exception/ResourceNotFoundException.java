package com.jwt.szs.exception;

import lombok.Getter;


@Getter
public class ResourceNotFoundException extends CustomRuntimeException {

    public ResourceNotFoundException() {
        super(ErrorCode.RESOURCE_NOT_FOUND);
    }

    public ResourceNotFoundException(String reason) {
        super(ErrorCode.RESOURCE_NOT_FOUND, reason);
    }
}
