package com.jwt.redis.config;

import com.jwt.redis.filter.JwtTokenFilter;
import com.jwt.redis.filter.strategy.CheckJwtCookieTokenStrategy;
import com.jwt.redis.filter.strategy.CheckJwtHeaderTokenStrategy;
import com.jwt.redis.filter.strategy.CheckJwtTokenStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Configuration
@RequiredArgsConstructor
public class AppConfig {


    @Bean
    public JwtTokenFilter jwtTokenCookieFilter(){
        return new JwtTokenFilter(checkJwtTokenStrategy());
    }

    @Bean
    public CheckJwtTokenStrategy checkJwtTokenStrategy(){
        return new CheckJwtHeaderTokenStrategy();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
