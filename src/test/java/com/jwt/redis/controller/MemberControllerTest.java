package com.jwt.redis.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jwt.redis.config.SecurityConfig;
import com.jwt.redis.exception.ErrorCode;
import com.jwt.redis.exception.ErrorResponse;
import com.jwt.redis.model.dto.AuthenticationRequest;
import com.jwt.redis.model.dto.MemberCreationRequest;
import com.jwt.redis.model.dto.MemberResponse;
import com.jwt.redis.model.type.JwtTokenType;
import com.jwt.redis.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void 회원가입() throws Exception {

        String json = usernamePasswordToJson("tmdghks", "123");

        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    @Test
    public void 회원가입_실패_요청값_검증() throws Exception {

        String json = usernamePasswordToJson("tmdghks", "");

        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        json = usernamePasswordToJson("", "123");

        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();
    }

    @Test
    public void 로그인() throws Exception {

        회원가입();

        String json = usernamePasswordToJson("tmdghks", "123");

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/members/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andReturn();

        Cookie[] cookies = mvcResult.getResponse().getCookies();

        Cookie accessTokenCookie = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals("accessToken"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("토큰 발급에 실패하였습니다."));

        Assertions.assertTrue(JwtTokenUtils.validate(accessTokenCookie.getValue()));
    }

    @Test
    public void 토큰검증() throws Exception {

        Cookie accessTokenCookie = generateAccessTokenCookie();

        mockMvc.perform(get("/api/test/token")
                        .cookie(accessTokenCookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void 토큰검증_실패_토큰없이전송() throws Exception {

        mockMvc.perform(get("/api/test/token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void 토큰재발급_실패_만료되지않은_액세스토큰() throws Exception {

        Cookie accessTokenCookie = generateAccessTokenCookie();
        Cookie refreshTokenCookie = generateRefreshTokenCookie();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/members/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(accessTokenCookie, refreshTokenCookie))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        ErrorCode errorCode = jsonToErrorCode(mvcResult.getResponse().getContentAsString());
        Assertions.assertEquals(errorCode, ErrorCode.NOT_YET_EXPIRED_TOKEN);
    }

    private ErrorCode jsonToErrorCode(String json) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);
        return ErrorCode.findByCode(errorResponse.getCode());
    }

    private String usernamePasswordToJson(String username, String password) {

        AuthenticationRequest request = AuthenticationRequest.builder()
                .username(username)
                .password(password)
                .build();

        Gson gson = new Gson();
        return gson.toJson(request);
    }

    private Cookie generateAccessTokenCookie() {

        MemberResponse memberResponse = MemberResponse.builder().id(1l).username("tmdghks").password("123").build();
        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS);
        return JwtTokenUtils.createAccessTokenCookie(token);
    }

    private Cookie generateRefreshTokenCookie() {

        MemberResponse memberResponse = MemberResponse.builder().id(1l).username("tmdghks").password("123").build();
        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);
        return JwtTokenUtils.createRefreshTokenCookie(token);
    }
}