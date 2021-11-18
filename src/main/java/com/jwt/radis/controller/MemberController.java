package com.jwt.radis.controller;

import com.jwt.radis.exception.ErrorCode;
import com.jwt.radis.exception.LRuntimeException;
import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberCreationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.model.type.JwtTokenType;
import com.jwt.radis.service.MemberService;
import com.jwt.radis.utils.JwtTokenUtils;
import com.jwt.radis.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MemberService memberService;

    @PostMapping("/authenticate")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest,
                                HttpServletResponse res) {
        final MemberResponse memberResponse = memberService.signIn(authenticationRequest);

        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS);

        final String refreshJwt = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);

        redisUtil.setDataExpire(refreshJwt, memberResponse.getId(), JwtTokenType.REFRESH.getValidationSeconds());

        Long data = redisUtil.getData(refreshJwt);

        log.info("data {}", data);

        res.addCookie(JwtTokenUtils.createCookie(JwtTokenType.ACCESS, token));

        res.addCookie(JwtTokenUtils.createCookie(JwtTokenType.REFRESH, refreshJwt));

        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody MemberCreationRequest memberCreationRequest) {

        memberService.signUp(memberCreationRequest);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/token", params = "access")
    public ResponseEntity accessToken(HttpServletRequest request, HttpServletResponse res) {

        String refreshToken = JwtTokenUtils.findCookie(request, JwtTokenType.REFRESH).getValue();

        String accessToken = JwtTokenUtils.findCookie(request, JwtTokenType.ACCESS).getValue();

        if(!JwtTokenUtils.isTokenExpired(accessToken)){
            throw new LRuntimeException(ErrorCode.DID_NOT_EXPIRED_TOKEN);
        }

        Long memberIdByRedis = redisUtil.getData(refreshToken);

        if (memberIdByRedis == null) {
            throw new LRuntimeException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long memberId = JwtTokenUtils.getId(refreshToken);

        if (!memberIdByRedis.equals(memberId)) {
            throw new LRuntimeException(ErrorCode.NOT_MATCHED_VALUE);
        }

        MemberResponse memberResponse = memberService.getById(memberId);

        final String generatedAccessToken = JwtTokenUtils.generateAccessToken(memberResponse);

        res.addCookie(JwtTokenUtils.createAccessTokenCookie(generatedAccessToken));

        return ResponseEntity.ok(generatedAccessToken);
    }
}
