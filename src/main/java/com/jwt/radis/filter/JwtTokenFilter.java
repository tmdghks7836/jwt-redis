package com.jwt.radis.filter;

import com.jwt.radis.model.dto.UserDetailsImpl;
import com.jwt.radis.model.type.JwtTokenType;
import com.jwt.radis.utils.CookieUtil;
import com.jwt.radis.utils.JwtTokenUtils;
import com.querydsl.core.util.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static java.util.Optional.ofNullable;

@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Get authorization header and validate
//        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (isEmpty(header) || !header.startsWith("Bearer ")) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        // Get jwt token and validate
//        final String token = header.split(" ")[1].trim();
//
//        if (!JwtTokenUtils.validate(token)) {
//            chain.doFilter(request, response);
//            return;
//        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null || cookies.length == 0) {
            chain.doFilter(request, response);
            return;
        }

        Cookie jwtCookie = CookieUtil.getCookie(
                request,
                JwtTokenType.ACCESS.getCookieName()
        );

        if (jwtCookie == null) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = jwtCookie.getValue();

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
        authentication.setDetails(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
