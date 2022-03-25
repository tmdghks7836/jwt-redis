package com.jwt.redis.filter;

import com.jwt.redis.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.redis.model.dto.UserDetailsImpl;
import com.jwt.redis.utils.JwtTokenUtils;
import com.querydsl.core.util.ArrayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenCookieFilter extends OncePerRequestFilter {

    private final CheckJwtTokenStrategy checkJwtTokenStrategy;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String token = checkJwtTokenStrategy.getTokenByRequest(request);

        if (StringUtils.hasText(token)) {
            authenticate(token);
        }

        chain.doFilter(request, response);
    }

    private void authenticate(String token) {

        // Get user identity and set it on the spring security context
        UserDetailsImpl userDetails = new UserDetailsImpl(
                JwtTokenUtils.getId(token),
                JwtTokenUtils.getUsername(token));

        String[] roles = JwtTokenUtils.getRoles(token);

        if (!ArrayUtils.isEmpty(roles)) {
            userDetails.setAuthorities(AuthorityUtils.createAuthorityList(roles));
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null,
                ofNullable(userDetails).map(UserDetails::getAuthorities).orElse(Collections.emptyList())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
