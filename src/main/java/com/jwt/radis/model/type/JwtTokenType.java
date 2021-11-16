package com.jwt.radis.model.type;

public enum JwtTokenType {
    ACCESS(1000L * 10), REFRESH(1000L * 60 * 24 * 2);


    private Long validationSeconds;

    JwtTokenType(Long validationSeconds) {
        this.validationSeconds = validationSeconds;
    }

    public Long getValidationSeconds() {
        return validationSeconds;
    }
}
