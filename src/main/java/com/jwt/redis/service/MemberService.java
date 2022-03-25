package com.jwt.redis.service;

import com.jwt.redis.exception.CustomRuntimeException;
import com.jwt.redis.exception.ErrorCode;
import com.jwt.redis.model.dto.AuthenticationRequest;
import com.jwt.redis.model.dto.MemberCreationRequest;
import com.jwt.redis.model.dto.MemberResponse;
import com.jwt.redis.model.entity.Member;
import com.jwt.redis.model.mapper.MemberMapper;
import com.jwt.redis.repository.MemberRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberService {

    @Autowired
    private MemberRepositorySupport memberRepositorySupport;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public MemberResponse authenticate(AuthenticationRequest authenticationRequest) {

        Optional<Member> memberOptional = memberRepositorySupport.findByUsername(authenticationRequest.getUsername());

        if (!memberOptional.isPresent()) {
            throw new CustomRuntimeException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Member member = memberOptional.get();

        if (!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(), member.getPassword())) {
            throw new CustomRuntimeException(ErrorCode.NOT_MATCHED_PASSWORD);
        }

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    @Transactional
    public void join(MemberCreationRequest memberCreationRequest) {

        Member member = new Member(
                memberCreationRequest.getUsername(),
                bCryptPasswordEncoder.encode(memberCreationRequest.getPassword())
        );

        memberRepositorySupport.save(member);
    }

    public MemberResponse getById(Long id){

        Member member = memberRepositorySupport.findById(id)
                .orElseThrow(() ->
                        new CustomRuntimeException(ErrorCode.RESOURCE_NOT_FOUND));

        return MemberMapper.INSTANCE.modelToDto(member);
    }
}
