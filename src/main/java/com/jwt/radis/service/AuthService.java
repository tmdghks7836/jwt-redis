package com.jwt.radis.service;

import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    MemberRepository memberRepository;

    public MemberResponse loginUser(AuthenticationRequest authenticationRequest){


      //  memberRepository.findById(au) authenticationRequest
        return null;
    }
}
