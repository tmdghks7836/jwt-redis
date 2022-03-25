package com.jwt.redis.filter.strategy;

import com.jwt.redis.model.type.JwtTokenType;
import com.jwt.redis.utils.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 쿠키 값으로 jwt token을 검증합니다.
 * */
public class CheckJwtCookieTokenStrategy implements CheckJwtTokenStrategy {

    @Override
    public String getTokenByRequest(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            return null;
        }

        Cookie jwtCookie = CookieUtil.getCookie(
                request,
                JwtTokenType.ACCESS.getCookieName()
        );

        if (jwtCookie == null) {
           return null;
        }

        // Get jwt token and validate
        return jwtCookie.getValue();
    }
}
