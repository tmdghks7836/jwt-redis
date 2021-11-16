package com.jwt.radis.service;

import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public MemberResponse loginUser(AuthenticationRequest authenticationRequest){


        encoder.matches()
      //  memberRepository.findById(au) authenticationRequest
        return null;
    }
}
