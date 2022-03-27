package com.jwt.szs.controller.advice;

import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Slf4j
@RequiredArgsConstructor
public class ValidateAccessTokenAdvice implements MethodInterceptor {

    private final String accessToken;

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {

        log.info("액세스 토큰이 만료되었는지 검사합니다.");

        if (!JwtTokenUtils.isTokenExpired(accessToken)) {
            throw new CustomRuntimeException(ErrorCode.NOT_YET_EXPIRED_TOKEN);
        }

        return invocation.proceed();
    }
}
