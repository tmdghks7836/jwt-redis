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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public MemberResponse authenticate(String username, String password) {

        Optional<Member> memberOptional = memberRepositorySupport.findByUsername(username);

        if (!memberOptional.isPresent()) {
            throw new UsernameNotFoundException(username +" not found");
        }

        Member member = memberOptional.get();

        if (!bCryptPasswordEncoder.matches(password, member.getPassword())) {
            throw new BadCredentialsException(ErrorCode.NOT_MATCHED_PASSWORD.getDescription());
        }

        return MemberMapper.INSTANCE.modelToDto(member);
    }

    @Transactional
    public Long join(MemberCreationRequest memberCreationRequest) {

        Member member = new Member(
                memberCreationRequest.getUsername(),
                bCryptPasswordEncoder.encode(memberCreationRequest.getPassword())
        );

        memberRepositorySupport.save(member);

        return member.getId();
    }

    public MemberResponse getById(Long id){

        Member member = memberRepositorySupport.findById(id)
                .orElseThrow(() ->
                        new CustomRuntimeException(ErrorCode.RESOURCE_NOT_FOUND));

        return MemberMapper.INSTANCE.modelToDto(member);
    }
}
