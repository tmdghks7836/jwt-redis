package com.jwt.szs.filter;

import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import com.jwt.szs.model.dto.AuthenticationUserPrinciple;
import com.jwt.szs.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final CheckJwtTokenStrategy checkJwtTokenStrategy;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        log.info("JwtTokenFilter.");

        String token = checkJwtTokenStrategy.getTokenByRequest(request);

        if (StringUtils.hasText(token) && JwtTokenUtils.validate(token)) {
            authorization(token);
        }

        chain.doFilter(request, response);
    }

    private void authorization(String token) {

        AuthenticationUserPrinciple authenticationUserPrinciple = new AuthenticationUserPrinciple(token);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                authenticationUserPrinciple, null,
                authenticationUserPrinciple.getAuthorities()
        );

//        //TODO 필요한 코드인지?
//        authentication.setDetails(userDetails);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
