package com.jwt.redis.config;

import com.jwt.redis.handler.CustomAuthenticationFailureHandler;
import com.jwt.redis.handler.CustomAuthenticationSuccessHandler;
import com.jwt.redis.handler.JwtAccessDeniedHandler;
import com.jwt.redis.handler.JwtAuthenticationEntryPoint;
import com.jwt.redis.filter.CustomAuthenticationFilter;
import com.jwt.redis.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final JwtTokenFilter jwtTokenFilter;

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // 커스텀 인증 필터
    @Bean
    public CustomAuthenticationFilter customAuthenticationProcessingFilter() throws Exception {
        CustomAuthenticationFilter filter = new CustomAuthenticationFilter("/api/v1/members/authenticate");
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(authenticationFailureHandler);
        filter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        return filter;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers(
                        "/h2-console/**", "/favicon.ico", "/error"
                        , "/csrf", "/v2/api-docs", "/configuration/**",
                        "/swagger*/**", "/webjars/**"
                );
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {


        httpSecurity
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()
                .cors()
                .and()
                .formLogin().disable()
                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers(apiPathToAllow()).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(customAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, CustomAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);
    }

    private String[] apiPathToAllow() {

        String apiV1Path = "/api/v1/";
        return new String[]{
                apiV1Path + "members/authenticate",
                apiV1Path + "members/join",
                apiV1Path + "members/token/re-issuance",
        };
    }
}