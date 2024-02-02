package com.kr.matitting.jwt.service;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.constant.Role;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest
class JwtServiceTest {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserService userService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    static String accessToken;
    static String refreshToken;
    @BeforeEach
    void clear(){
        redisTemplate.keys("*").stream().forEach(k-> {
            redisTemplate.delete(k);
        });
    }
//    @BeforeEach
//    void 토큰발급() {
//        //로그인 후 토큰 발급!!
//        UserSignUpDto userSignUpDto = new UserSignUpDto("12345", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        User user = userService.signUp(userSignUpDto);
//        accessToken = jwtService.createAccessToken(user);
//        refreshToken = jwtService.createRefreshToken(user);
//        jwtService.updateRefreshToken(user.getSocialId(), refreshToken);
//    }
    @Test
    void 토큰재발급_성공() {
        //when
        String renew_accessToken = jwtService.renewToken(refreshToken);
        DecodedJWT tokenValid = jwtService.isTokenValid(renew_accessToken);
        String socialId = tokenValid.getClaim("socialId").asString();
        String role = tokenValid.getClaim("role").asString();

        //then
        assertThat(socialId).isEqualTo("12345");
        assertThat(role).isEqualTo(Role.USER.getKey());
    }

    @Test
    void 토큰재발급_실패_RefreshToken_유효X() {
        //when, then
        assertThrows(JWTDecodeException.class, () -> jwtService.renewToken("213897321798321987321978321978312987321987312978"));
    }

    @Test
    void 토큰재발급_실패_RefreshToken_없음() {
        //when, then
        redisUtil.deleteData("12345");
        assertThrows(TokenException.class, () -> jwtService.renewToken(refreshToken));
    }
}