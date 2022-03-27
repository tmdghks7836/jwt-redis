package com.jwt.redis.controller.advice;

import com.jwt.redis.exception.CustomRuntimeException;
import com.jwt.redis.exception.ErrorCode;
import com.jwt.redis.model.type.JwtTokenType;
import com.jwt.redis.utils.JwtTokenUtils;
import com.jwt.redis.utils.RedisUtil;
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

    private final HttpServletRequest request;

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {

        log.info("리프레시 토큰의 만료기한과 redis에 저장된 토큰값을 확인합니다.");

        String refreshToken = JwtTokenUtils.findCookie(request, JwtTokenType.REFRESH).getValue();

        Long memberIdByRedis = redisUtil.getData(refreshToken);

        if (memberIdByRedis == null) {
            throw new CustomRuntimeException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long memberId = JwtTokenUtils.getId(refreshToken);

        if (!memberIdByRedis.equals(memberId)) {
            throw new CustomRuntimeException(ErrorCode.NOT_MATCHED_VALUE);
        }

        Object result = invocation.proceed();

        return result;
    }
}
