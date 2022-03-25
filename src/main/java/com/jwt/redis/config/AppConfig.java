package com.jwt.redis.config;

import com.jwt.redis.filter.JwtTokenFilter;
import com.jwt.redis.filter.strategy.CheckJwtCookieTokenStrategy;
import com.jwt.redis.filter.strategy.CheckJwtTokenStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppConfig {


    @Bean
    public JwtTokenFilter jwtTokenCookieFilter(){
        return new JwtTokenFilter(checkJwtTokenStrategy());
    }

    CheckJwtTokenStrategy checkJwtTokenStrategy(){
        return new CheckJwtCookieTokenStrategy();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
