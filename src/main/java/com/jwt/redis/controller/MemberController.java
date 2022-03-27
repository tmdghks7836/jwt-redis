package com.jwt.redis.controller;

import com.jwt.redis.controller.advice.JwtTokenService;
import com.jwt.redis.controller.advice.ValidateAccessTokenAdvice;
import com.jwt.redis.controller.advice.ValidateTokenRedisAdvice;
import com.jwt.redis.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.redis.model.dto.AuthenticationRequest;
import com.jwt.redis.model.dto.MemberCreationRequest;
import com.jwt.redis.model.type.JwtTokenType;
import com.jwt.redis.service.MemberService;
import com.jwt.redis.utils.JwtTokenUtils;
import com.jwt.redis.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Slf4j
@RestController
@Api(tags = "유저")
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final RedisUtil redisUtil;

    private final MemberService memberService;

    private final CheckJwtTokenStrategy jwtTokenStrategy;

    private final JwtTokenService target;

    @PostMapping(value = "/authenticate")
    @ApiOperation(value = "로그인")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest) {

        log.info("spring security 에서 login 인증기능을 담당합니다.");
        return null;
    }

    @PostMapping("/join")
    @ApiOperation(value = "회원가입")
    @ResponseStatus(HttpStatus.CREATED)
    public void join(@RequestBody @Valid MemberCreationRequest memberCreationRequest) {

        memberService.join(memberCreationRequest);
    }

    @GetMapping(value = "/token/re-issuance")
    @ApiOperation(value = "access token 재발급")
    public ResponseEntity accessToken(HttpServletRequest request, HttpServletResponse res) {

        ProxyFactory proxyFactory = new ProxyFactory(target); //프록시 팩토리에 원하는 클래스를 주입
        proxyFactory.addAdvice(new ValidateAccessTokenAdvice(jwtTokenStrategy, request)); // 공통으로 실행할 advice 객체 주입
        proxyFactory.addAdvice(new ValidateTokenRedisAdvice(redisUtil, request));

        JwtTokenService proxy = (JwtTokenService) proxyFactory.getProxy();

        String refreshToken = JwtTokenUtils.findCookie(request, JwtTokenType.REFRESH).getValue();
        String token = proxy.ReIssuanceAccessToken(refreshToken);

        return ResponseEntity.ok(token);
    }
}
