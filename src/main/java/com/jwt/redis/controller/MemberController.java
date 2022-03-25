package com.jwt.redis.controller;

import com.jwt.redis.exception.CustomRuntimeException;
import com.jwt.redis.exception.ErrorCode;
import com.jwt.redis.model.dto.AuthenticationRequest;
import com.jwt.redis.model.dto.MemberCreationRequest;
import com.jwt.redis.model.dto.MemberResponse;
import com.jwt.redis.model.type.JwtTokenType;
import com.jwt.redis.service.MemberService;
import com.jwt.redis.utils.JwtTokenUtils;
import com.jwt.redis.utils.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@Api(tags = "유저")
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final RedisUtil redisUtil;

    private final MemberService memberService;

    @PostMapping("/authenticate")
    @ApiOperation(value = "로그인")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest,
                                HttpServletResponse res) {

        final MemberResponse memberResponse = memberService.authenticate(authenticationRequest);

        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS);

        final String refreshJwt = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);

        redisUtil.setDataContainsExpire(refreshJwt, memberResponse.getId(), JwtTokenType.REFRESH.getValidationSeconds());

        Long data = redisUtil.getData(refreshJwt);

        log.info("data {}", data);

        res.addCookie(JwtTokenUtils.createCookie(JwtTokenType.ACCESS, token));

        res.addCookie(JwtTokenUtils.createCookie(JwtTokenType.REFRESH, refreshJwt));

        return ResponseEntity.ok(token);
    }

    @PostMapping("/join")
    @ApiOperation(value = "회원가입")
    public ResponseEntity signUp(@RequestBody MemberCreationRequest memberCreationRequest) {

        memberService.join(memberCreationRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/token/re-issuance")
    @ApiOperation(value = "access token 재발급")
    public ResponseEntity accessToken(HttpServletRequest request, HttpServletResponse res) {

        String refreshToken = JwtTokenUtils.findCookie(request, JwtTokenType.REFRESH).getValue();

        String accessToken = JwtTokenUtils.findCookie(request, JwtTokenType.ACCESS).getValue();

        if (!JwtTokenUtils.isTokenExpired(accessToken)) {
            throw new CustomRuntimeException(ErrorCode.NOT_YET_EXPIRED_TOKEN);
        }

        Long memberIdByRedis = redisUtil.getData(refreshToken);

        if (memberIdByRedis == null) {
            throw new CustomRuntimeException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long memberId = JwtTokenUtils.getId(refreshToken);

        if (!memberIdByRedis.equals(memberId)) {
            throw new CustomRuntimeException(ErrorCode.NOT_MATCHED_VALUE);
        }

        MemberResponse memberResponse = memberService.getById(memberId);

        final String generatedAccessToken = JwtTokenUtils.generateAccessToken(memberResponse);

        res.addCookie(JwtTokenUtils.createAccessTokenCookie(generatedAccessToken));

        return ResponseEntity.ok(generatedAccessToken);
    }
}
