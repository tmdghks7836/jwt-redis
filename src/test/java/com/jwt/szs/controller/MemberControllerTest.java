package com.jwt.szs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jwt.szs.exception.ErrorCode;
import com.jwt.szs.exception.ErrorResponse;
import com.jwt.szs.model.dto.AuthenticationRequest;
import com.jwt.szs.model.dto.MemberResponse;
import com.jwt.szs.model.dto.UserDetailsImpl;
import com.jwt.szs.model.type.JwtTokenType;
import com.jwt.szs.service.CustomUserDetailsService;
import com.jwt.szs.service.MemberService;
import com.jwt.szs.utils.JwtTokenUtils;
import com.jwt.szs.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Slf4j
@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private RedisUtil redisUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private MemberService memberService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    public void ????????????() throws Exception {

        String json = usernamePasswordToJson("tmdghks", "123");

        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();
    }

    @Test
    public void ????????????_??????_?????????_??????() throws Exception {

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
    public void ?????????() throws Exception {
        MemberResponse memberResponse = MemberResponse.builder().username("tmedghks").id(1l).build();
        String password = "123";

        Mockito.when(memberService.authenticate(memberResponse.getUsername(), password))
                .thenReturn(memberResponse);
        Mockito.when(customUserDetailsService.loadUserByUsername(memberResponse.getUsername()))
                .thenReturn(new UserDetailsImpl(
                        memberResponse.getId(),
                        memberResponse.getUsername(),
                        passwordEncoder.encode(password)
                ));

        String json = usernamePasswordToJson(memberResponse.getUsername(), password);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/members/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        HashMap<String, String> hashMap = new ObjectMapper()
                .readValue(mvcResult.getResponse().getContentAsString(), HashMap.class);
        String token = hashMap.get("token");

        Assertions.assertTrue(JwtTokenUtils.validate(token));
    }

    @Test
    public void ????????????() throws Exception {

        String token = generateAccessToken();

        mockMvc.perform(get("/api/test/token")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @Test
    public void ????????????_??????_??????????????????() throws Exception {

        mockMvc.perform(get("/api/test/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", ""))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void ???????????????_MockBean() throws Exception {

        MemberResponse memberResponse = MemberResponse.builder().id(123l).username("tmdghks").build();
        String token = generateExpiredAccessToken(memberResponse);
        Cookie refreshTokenCookie = generateRefreshTokenCookie(memberResponse.getId());

        Mockito.when(redisUtil.<Long>getData(refreshTokenCookie.getValue()))
                .thenReturn(Optional.of(memberResponse.getId()));
        Mockito.when(memberService.getById(memberResponse.getId()))
                .thenReturn(memberResponse);

        MvcResult mvcResult = mockMvc.perform(get("/api/v1/members/token/re-issuance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .cookie(refreshTokenCookie))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        log.info("????????? ??????????????? response : {}", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void ???????????????_??????_????????????????????????_???????????????() throws Exception {

        String token = generateAccessToken();
        Cookie refreshTokenCookie = generateRefreshTokenCookie(1l);
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

    private Cookie generateRefreshTokenCookie(Long id) {

        MemberResponse memberResponse = MemberResponse.builder().id(id).username("tmdghks").build();
        final String token = JwtTokenUtils.generateToken(memberResponse, JwtTokenType.REFRESH);
        return JwtTokenUtils.createRefreshTokenCookie(token);
    }
}