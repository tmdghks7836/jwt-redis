package com.jwt.radis.service;

import com.jwt.radis.exception.CustomRuntimeException;
import com.jwt.radis.exception.ErrorCode;
import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.model.entity.Member;
import com.jwt.radis.model.mapper.MemberMapper;
import com.jwt.radis.repository.MemberRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepositorySupport memberRepositorySupport;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberResponse getSigningUser(AuthenticationRequest authenticationRequest) {

        Optional<Member> memberOptional = memberRepositorySupport.findByUsername(authenticationRequest.getUsername());

        if (!memberOptional.isPresent()) {
            throw new CustomRuntimeException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Member member = memberOptional.get();

        if(!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(), member.getPassword())){
            throw new CustomRuntimeException(ErrorCode.NOT_MATCHED_PASSWORD);
        }

        return MemberMapper.INSTANCE.modelToDto(member);

    }
}
