package com.jwt.szs.filter.strategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt token 으로 인증하는 다양한 전략 패턴을 사용합니다.
 * */
public interface CheckJwtTokenStrategy {

    void setResponseToken(HttpServletResponse response, String token);

    String getTokenByRequest(HttpServletRequest request);
}
