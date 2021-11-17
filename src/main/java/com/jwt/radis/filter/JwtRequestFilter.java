package com.jwt.radis.filter;

import com.jwt.radis.model.entity.Member;
import com.jwt.radis.model.type.JwtTokenType;
import com.jwt.radis.repository.MemberRepositorySupport;
import com.jwt.radis.service.CustomUserDetailsService;
import com.jwt.radis.utils.CookieUtil;
import com.jwt.radis.utils.JwtTokenUtils;
import com.jwt.radis.utils.JwtUtil;
import com.jwt.radis.utils.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Deprecated
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MemberRepositorySupport memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final Cookie jwtToken = CookieUtil.getCookie(httpServletRequest, JwtTokenType.ACCESS.getCookieName());

        String username = null;
        String jwt = null;
        String refreshJwt = null;
        String refreshUname = null;

        try {
            if (jwtToken != null) {
                jwt = jwtToken.getValue();
                username = JwtUtil.getUsername(jwt);
            }
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (JwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            Cookie refreshToken = CookieUtil.getCookie(httpServletRequest, JwtTokenType.REFRESH.getCookieName());
            if (refreshToken != null) {
                refreshJwt = refreshToken.getValue();
            }
        } catch (Exception e) {

        }

        try {
            if (refreshJwt != null) {
                refreshUname = redisUtil.getData(refreshJwt);

                if (refreshUname.equals(JwtUtil.getUsername(refreshJwt))) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(refreshUname);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    Member member = memberRepository.findByUsername(refreshUname).get();
                    member.setUsername(refreshUname);
                    String newToken = JwtTokenUtils.generateToken(member.getUsername(), JwtTokenType.REFRESH);

                    Cookie newAccessToken = JwtTokenUtils.createCookie(JwtTokenType.ACCESS, newToken);
                    httpServletResponse.addCookie(newAccessToken);
                }
            }
        } catch (ExpiredJwtException e) {

        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}