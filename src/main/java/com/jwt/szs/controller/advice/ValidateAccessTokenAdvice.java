package com.jwt.szs.controller.advice;

import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
public class ValidateAccessTokenAdvice implements MethodInterceptor {

    private final CheckJwtTokenStrategy jwtTokenStrategy;

    private final HttpServletRequest request;

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {

        log.info("액세스 토큰이 만료되었는지 검사합니다.");

        String token = jwtTokenStrategy.getTokenByRequest(request);

        if (!JwtTokenUtils.isTokenExpired(token)) {
            throw new CustomRuntimeException(ErrorCode.NOT_YET_EXPIRED_TOKEN);
        }

        Object result = invocation.proceed();

        return result;
    }
}
