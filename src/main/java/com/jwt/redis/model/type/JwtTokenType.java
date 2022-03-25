package com.jwt.redis.model.type;

import lombok.Getter;

@Getter
public enum JwtTokenType {
    ACCESS("accessToken", 1000 * 60 * 30), REFRESH("refreshToken", 1000 * 60 * 60 * 24 * 2);

    private int validationSeconds;

    private String cookieName;

    JwtTokenType(String cookieName, int validationSeconds) {
        this.validationSeconds = validationSeconds;
        this.cookieName = cookieName;
    }
}
