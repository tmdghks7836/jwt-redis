package com.jwt.radis.model.type;

import lombok.Getter;

@Getter
public enum JwtTokenType {
    ACCESS("accessToken", 1000L * 60 * 30), REFRESH("refreshToken", 1000L * 60 * 60 * 24 * 2);

    private Long validationSeconds;

    private String cookieName;

    JwtTokenType(String cookieName, Long validationSeconds) {
        this.validationSeconds = validationSeconds;
        this.cookieName = cookieName;
    }
}
