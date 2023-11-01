package com.kr.matitting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.mock.WithCustomMockUser;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.service.UserService;
import com.kr.matitting.util.RedisUtil;
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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class OAuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    static String accessToken;
    static String refreshToken;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .socialId("453534")
                .socialType(SocialType.KAKAO)
                .email("test@naver.com")
                .nickname("새싹개발자")
                .age(26)
                .imgUrl("증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    @BeforeEach
    void 토큰발급() {
        //로그인 후 토큰 발급!!
        UserSignUpDto userSignUpDto = new UserSignUpDto("12345", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
        User user = userService.signUp(userSignUpDto);
        accessToken = jwtService.createAccessToken(user);
        refreshToken = jwtService.createRefreshToken(user);
        jwtService.updateRefreshToken(user.getSocialId(), refreshToken);
    }

    @BeforeEach
    void clear(){
        redisTemplate.keys("*").stream().forEach(k-> {
            redisTemplate.delete(k);
        });
    }

    @Test
    @WithCustomMockUser
    void 회원가입_성공() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialId", "213321321")
                        .param("socialType", String.valueOf(SocialType.KAKAO))
                        .param("email", "test1@naver.com")
                        .param("nickname", "안경잡이")
                        .param("age", "25")
                        .param("imgUrl", "증명.jpg")
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원가입_실패_socialId_없음() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialType", String.valueOf(SocialType.KAKAO))
                        .param("email", "test1@naver.com")
                        .param("nickname", "안경잡이")
                        .param("age", "25")
                        .param("imgUrl", "증명.jpg")
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원가입_실패_email_없음() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialId", "213321321")
                        .param("nickname", "안경잡이")
                        .param("age", "25")
                        .param("imgUrl", "증명.jpg")
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원가입_실패_nickname_없음() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialId", "213321321")
                        .param("socialType", String.valueOf(SocialType.KAKAO))
                        .param("email", "test1@naver.com")
                        .param("age", "25")
                        .param("imgUrl", "증명.jpg")
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원가입_실패_age_없음() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialId", "213321321")
                        .param("socialType", String.valueOf(SocialType.KAKAO))
                        .param("email", "test1@naver.com")
                        .param("nickname", "안경잡이")
                        .param("imgUrl", "증명.jpg")
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원가입_성공_imgUrl_없음() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialId", "213321321")
                        .param("socialType", String.valueOf(SocialType.KAKAO))
                        .param("email", "test1@naver.com")
                        .param("nickname", "안경잡이")
                        .param("age", "25")
                        .param("imgUrl", "증명.jpg")
                        .param("gender", String.valueOf(Gender.MALE)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원가입_실패_gender_없음() throws Exception{
        //given, when, then
        mockMvc.perform(post("/oauth2/signup")
                        .with(csrf())
                        .header("X-AUTH-TOKEN", "aaaaaaa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("socialId", "213321321")
                        .param("socialType", String.valueOf(SocialType.KAKAO))
                        .param("email", "test1@naver.com")
                        .param("nickname", "안경잡이")
                        .param("age", "25")
                        .param("imgUrl", "증명.jpg"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 회원탈퇴_성공() throws Exception {
        //given
        User user = userRepository.findBySocialId("453534").get();

        //when
        String accessToken = jwtService.createAccessToken(user);

        // then
        mockMvc.perform(delete("/oauth2/withdraw")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), "BEARER "+accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());

    }

    @Test
    @WithCustomMockUser
    void 회원탈퇴_실패_socialId_없음() throws Exception {
        //given
        User user = userRepository.findBySocialId("453534").get();
        User newUser = User.builder().socialId("123132").socialType(SocialType.NAVER).email("random@google.com").nickname("테두").age(30).imgUrl("헝그리.jpg").gender(Gender.FEMALE).role(Role.USER).build();

        //when
        String accessToken = jwtService.createAccessToken(newUser);

        // then
        mockMvc.perform(delete("/oauth2/withdraw")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), "BEARER "+accessToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 토큰재발급_성공() throws Exception {
        //when, then
        mockMvc.perform(get("/oauth2/renew-token")
                        .with(csrf())
                        .header(jwtService.getRefreshHeader(), "BEARER "+refreshToken))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(print());
    }

    @Test
    @WithCustomMockUser
    void 토큰재발급_실패_RefreshToken_유효X() throws Exception {
        //when, then
        mockMvc.perform(get("/oauth2/renew-token")
                        .with(csrf())
                        .header(jwtService.getRefreshHeader(), "BEARER "+accessToken))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 토큰재발급_실패_RefreshToken_없음() throws Exception {
        //given
        User newUser = User.builder().socialId("123132").socialType(SocialType.NAVER).email("random@google.com").nickname("테두").age(30).imgUrl("헝그리.jpg").gender(Gender.FEMALE).role(Role.USER).build();
        String newRefreshToken = jwtService.createRefreshToken(newUser);
        //when, then
        mockMvc.perform(get("/oauth2/renew-token")
                        .with(csrf())
                        .header(jwtService.getRefreshHeader(), "BEARER "+newRefreshToken))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(print());
    }

}