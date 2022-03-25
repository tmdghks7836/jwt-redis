package com.jwt.redis.filter.strategy;

import javax.servlet.http.HttpServletRequest;

/**
 * jwt token을 가져오는 전략 패턴을 사용합니다.
 * */
public interface CheckJwtTokenStrategy {

    String getTokenByRequest(HttpServletRequest request);
}
