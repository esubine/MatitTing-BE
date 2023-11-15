package com.kr.matitting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    static String accessToken;
    static String refreshToken;
    static User user;
    @BeforeEach
    void 토큰발급() {
        //로그인 후 토큰 발급!!
        UserSignUpDto userSignUpDto = new UserSignUpDto("132321321321", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
        user = userService.signUp(userSignUpDto);
        accessToken = "BEARER "+jwtService.createAccessToken(user);
        refreshToken = "BEARER "+jwtService.createRefreshToken(user);
        jwtService.updateRefreshToken(user.getSocialId(), refreshToken);
    }

    @BeforeEach
    void clear(){
        redisTemplate.keys("*").stream().forEach(k-> {
            redisTemplate.delete(k);
        });
    }

    @Test
    void 프로필_업데이트_성공_사진() throws Exception {
        //given
        String userUpdateDto = objectMapper.writeValueAsString(new UserUpdateDto(null, "새로운_증명_사진.jpg"));

        //when, then
        mockMvc.perform(patch("/api/profile")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 프로필_업데이트_성공_닉네임() throws Exception {
        //given
        String userUpdateDto = objectMapper.writeValueAsString(new UserUpdateDto("취업 뿌시자", null));

        //when, then
        mockMvc.perform(patch("/api/profile")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateDto))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 프로필_업데이트_실패_ID_Null() throws Exception {
        //when, then
        mockMvc.perform(patch("/api/profile")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("nickname", "취업 뿌시자.jpg"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

}