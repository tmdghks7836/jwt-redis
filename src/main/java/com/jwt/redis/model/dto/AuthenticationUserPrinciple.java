package com.jwt.redis.model.dto;

import com.jwt.redis.utils.JwtTokenUtils;
import com.querydsl.core.util.ArrayUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthenticationUserPrinciple implements UserDetails {

    private String password;

    private String token;

    private List<GrantedAuthority> authorities = new ArrayList<>();

    private boolean enabled = true;

    //인가 플로우일때 생성
    public AuthenticationUserPrinciple(String token) {
        this.token = token;
    }

    public List<GrantedAuthority> getAuthorities() {

        String[] roles = JwtTokenUtils.getRoles(token);

        if (ArrayUtils.isEmpty(roles)) {
            return Collections.emptyList();
        }
        return AuthorityUtils.createAuthorityList(roles);
    }

    public Long getId() {
        return JwtTokenUtils.getId(token);
    }

    public String getUsername() {
        return JwtTokenUtils.getUsername(token);
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }
}
