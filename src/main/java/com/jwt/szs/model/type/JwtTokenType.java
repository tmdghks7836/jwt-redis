package com.jwt.szs.model.type;

import com.jwt.szs.exception.ResourceNotFoundException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum JwtTokenType {
    ACCESS("accessToken", 1000 * 60 * 30), REFRESH("refreshToken", 1000 * 60 * 60 * 24 * 2);

    private int validationSeconds;

    private String cookieName;

    JwtTokenType(String cookieName, int validationSeconds) {
        this.validationSeconds = validationSeconds;
        this.cookieName = cookieName;
    }

    public static JwtTokenType findByCookieName(String cookieName){

        return Arrays.stream(JwtTokenType.values())
                .filter(jwtTokenType -> jwtTokenType.getCookieName().equals(cookieName))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException());
    }
}
