package com.jwt.szs.service;

import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final MemberService memberService;


    public String ReIssuanceAccessToken(String refreshToken){

        log.info("액세스 토큰을 재발급합니다.");

        Long memberId = JwtTokenUtils.getId(refreshToken);
        MemberResponse memberResponse = memberService.getById(memberId);

        return JwtTokenUtils.generateAccessToken(memberResponse);
    }
}
