package com.jwt.szs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ErrorResponse;
import com.jwt.szs.model.dto.AuthenticationRequest;
import com.jwt.szs.model.dto.MemberCreationRequest;
import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.MemberService;
import com.jwt.szs.utils.JwtTokenUtils;
import com.jwt.szs.utils.RedisUtil;
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
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MemberService memberService;

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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> hashMap = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), HashMap.class);

        String token = hashMap.get("token");

        Assertions.assertTrue(JwtTokenUtils.validate(token));
    }

    @Test
    public void 토큰검증() throws Exception {

        String token = generateAccessToken();

        mockMvc.perform(get("/api/test/token")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void 토큰검증_실패_토큰없이전송() throws Exception {

        mockMvc.perform(get("/api/test/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void 토큰재발급() throws Exception {

        String username = "tmdghks";
        String password = "123";
        Long registeredId = memberService.join(new MemberCreationRequest(username, password));

        String token = generateExpiredAccessToken(new MemberResponse(registeredId, username));

        Cookie refreshTokenCookie = generateRefreshTokenCookie();
        redisUtil.setDataContainsExpireDate(refreshTokenCookie.getValue(),
                registeredId, JwtTokenType.REFRESH.getValidationSeconds());

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/members/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .cookie(refreshTokenCookie))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        log.info("액세스 토큰재발급 response : {}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void 토큰재발급_실패_아직만료되지않은_액세스토큰() throws Exception {

        String token = generateAccessToken();
        Cookie refreshTokenCookie = generateRefreshTokenCookie();
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/members/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .cookie(refreshTokenCookie))
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

    private String generateAccessToken() {

        MemberResponse memberResponse = MemberResponse.builder().id(1l).username("tmdghks").build();
        return JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS);
    }

    private String generateExpiredAccessToken(MemberResponse memberResponse) {

        return JwtTokenUtils.generateToken(memberResponse, JwtTokenType.ACCESS, -1l);
    }

    private Cookie generateRefreshTokenCookie() {

        MemberResponse memberResponse = MemberResponse.builder().id(1l).username("tmdghks").build();
        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);
        return JwtTokenUtils.createRefreshTokenCookie(token);
    }
}