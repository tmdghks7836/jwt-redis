package com.jwt.radis.controller;

import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.model.entity.Member;
import com.jwt.radis.model.type.JwtTokenType;
import com.jwt.radis.service.AuthService;
import com.jwt.radis.utils.CookieUtil;
import com.jwt.radis.utils.JwtTokenUtils;
import com.jwt.radis.utils.JwtUtil;
import com.jwt.radis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MemberController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest,
                                HttpServletRequest req,
                                HttpServletResponse res) {
        try {

            final MemberResponse memberResponse = authService.loginUser(authenticationRequest);

            final String token = JwtTokenUtils.generateToken(memberResponse.getUsername(), JwtTokenType.ACCESS);

            final String refreshJwt = JwtTokenUtils.generateToken(memberResponse.getUsername(), JwtTokenType.REFRESH);

            Cookie accessToken = CookieUtil.createCookie(JwtUtil.ACCESS_TOKEN_NAME, token);
            Cookie refreshToken = CookieUtil.createCookie(JwtUtil.REFRESH_TOKEN_NAME, refreshJwt);

            redisUtil.setDataExpire(refreshJwt, memberResponse.getUsername(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

            res.addCookie(accessToken);
            res.addCookie(refreshToken);

            return ResponseEntity.ok(token);
        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }
}
