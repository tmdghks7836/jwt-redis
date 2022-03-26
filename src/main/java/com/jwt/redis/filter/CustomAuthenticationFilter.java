package com.jwt.redis.filter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jwt.redis.model.dto.MemberResponse;
import com.jwt.redis.model.type.JwtTokenType;
import com.jwt.redis.service.MemberService;
import com.jwt.redis.utils.HttpUtils;
import com.jwt.redis.utils.JwtTokenUtils;
import com.jwt.redis.utils.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MemberService memberService;

    public CustomAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, ServletException, IOException {


        String body = HttpUtils.getBody(request);

        JsonObject usernamePasswordJson = JsonParser.parseString(body).getAsJsonObject();
        String username = usernamePasswordJson.get("username").getAsString();
        String password = usernamePasswordJson.get("password").getAsString();

        final MemberResponse memberResponse = memberService.authenticate(username, password);

        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS);

        final String refreshJwt = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);

        redisUtil.setDataContainsExpire(refreshJwt, memberResponse.getId(), JwtTokenType.REFRESH.getValidationSeconds());

        Long data = redisUtil.getData(refreshJwt);

        log.info("data {}", data);

        response.addCookie(JwtTokenUtils.createAccessTokenCookie(token));

        response.addCookie(JwtTokenUtils.createRefreshTokenCookie(refreshJwt));

        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
