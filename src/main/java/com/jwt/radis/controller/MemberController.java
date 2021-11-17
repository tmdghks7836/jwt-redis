package com.jwt.radis.controller;

import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberCreationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.model.type.JwtTokenType;
import com.jwt.radis.service.MemberService;
import com.jwt.radis.utils.CookieUtil;
import com.jwt.radis.utils.JwtTokenUtils;
import com.jwt.radis.utils.JwtUtil;
import com.jwt.radis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api")
public class MemberController {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody AuthenticationRequest authenticationRequest,
                                HttpServletResponse res) {
        try {

            final MemberResponse memberResponse = memberService.getSigningUser(authenticationRequest);

            final String token = JwtTokenUtils.generateToken(memberResponse.getUsername(), JwtTokenType.ACCESS);

            final String refreshJwt = JwtTokenUtils.generateToken(memberResponse.getUsername(), JwtTokenType.REFRESH);

            Cookie accessToken = JwtTokenUtils.createCookie(JwtTokenType.ACCESS, token);

            Cookie refreshToken = JwtTokenUtils.createCookie(JwtTokenType.REFRESH, refreshJwt);

            redisUtil.setDataExpire(refreshJwt, memberResponse.getUsername(), JwtUtil.REFRESH_TOKEN_VALIDATION_SECOND);

            res.addCookie(accessToken);

            res.addCookie(refreshToken);

            return ResponseEntity.ok(token);
        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/signup")
    public ResponseEntity signUp(@RequestBody MemberCreationRequest memberCreationRequest) {

        memberService.signUp(memberCreationRequest);

        return ResponseEntity.ok().build();
    }
}
