package com.jwt.redis.filter.strategy;

import com.jwt.redis.utils.JwtTokenUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * header의 Authorization : Bearer token 으로 검증합니다.
 * */
public class CheckJwtHeaderTokenStrategy implements CheckJwtTokenStrategy {

    @Override
    public String getTokenByRequest(HttpServletRequest request) {

        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
           return null;
        }

        final String token = header.split(" ")[1].trim();

        if (!JwtTokenUtils.validate(token)) {
            return null;
        }

        return token;
    }
}
