package com.jwt.radis.service;

import com.jwt.radis.exception.LRuntimeException;
import com.jwt.radis.exception.ErrorCode;
import com.jwt.radis.model.dto.AuthenticationRequest;
import com.jwt.radis.model.dto.MemberCreationRequest;
import com.jwt.radis.model.dto.MemberResponse;
import com.jwt.radis.model.entity.Member;
import com.jwt.radis.model.mapper.MemberMapper;
import com.jwt.radis.repository.MemberRepositorySupport;
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

    public MemberResponse signIn(AuthenticationRequest authenticationRequest) {

        Optional<Member> memberOptional = memberRepositorySupport.findByUsername(authenticationRequest.getUsername());

        if (!memberOptional.isPresent()) {
            throw new LRuntimeException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Member member = memberOptional.get();

        if (!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(), member.getPassword())) {
            throw new LRuntimeException(ErrorCode.NOT_MATCHED_PASSWORD);
        }

        return MemberMapper.INSTANCE.modelToDto(member);

    }

    @Transactional
    public void signUp(MemberCreationRequest memberCreationRequest) {

        Member member = new Member(
                memberCreationRequest.getUsername(),
                bCryptPasswordEncoder.encode(memberCreationRequest.getPassword())
        );

        memberRepositorySupport.save(member);
    }

    public MemberResponse getById(Long id){

        Member member = memberRepositorySupport.findById(id)
                .orElseThrow(() ->
                        new LRuntimeException(ErrorCode.RESOURCE_NOT_FOUND));

        return MemberMapper.INSTANCE.modelToDto(member);
    }
}
