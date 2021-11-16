package com.jwt.radis.service;

import com.jwt.radis.model.dto.UserDetailsImpl;
import com.jwt.radis.model.entity.Member;
import com.jwt.radis.repository.MemberRepositorySupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepositorySupport memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        Optional<Member> memberOptional = memberRepository.findByUsername(username);

        if (!memberOptional.isPresent()) {
            throw new RuntimeException();
        }

        Member member = memberOptional.get();
        return new UserDetailsImpl(member.getId(), member.getUsername());
    }
}
