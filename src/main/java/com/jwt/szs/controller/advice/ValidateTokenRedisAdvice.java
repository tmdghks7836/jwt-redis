package com.jwt.szs.controller.advice;

import com.jwt.szs.exception.CustomRuntimeException;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.utils.JwtTokenUtils;
import com.jwt.szs.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
public class ValidateTokenRedisAdvice implements MethodInterceptor {

    private final RedisUtil redisUtil;

    private final String refreshToken;

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {

        log.info("리프레시 토큰의 만료기한과 redis에 저장된 토큰값을 확인합니다.");

        Long memberIdByRedis = redisUtil.<Long>getData(refreshToken)
                .orElseThrow(() -> new CustomRuntimeException(ErrorCode.REFRESH_TOKEN_EXPIRED));

        Long memberId = JwtTokenUtils.getId(refreshToken);

        if (!memberIdByRedis.equals(memberId)) {
            throw new CustomRuntimeException(ErrorCode.NOT_MATCHED_VALUE);
        }

        return invocation.proceed();
    }
}
