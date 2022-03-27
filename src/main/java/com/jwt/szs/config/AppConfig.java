package com.jwt.szs.config;

import com.jwt.szs.filter.JwtTokenFilter;
import com.jwt.szs.filter.strategy.CheckJwtHeaderTokenStrategy;
import com.jwt.szs.filter.strategy.CheckJwtTokenStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
