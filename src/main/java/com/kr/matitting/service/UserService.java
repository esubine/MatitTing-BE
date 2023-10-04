package com.kr.matitting.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisUtil redisUtil;

    public String signUp(User user) {
        User createdUser = userRepository.save(user);
        return createdUser.getEmail();
    }

    public void logout(String accessToken) {
        DecodedJWT decodedJWT = jwtService.isTokenValid(accessToken);
        String socialId = decodedJWT.getClaim("id").asString();

        //expired 시간 check
        Long expiration = jwtService.getExpiration(accessToken);

        //redis에서 로그아웃 user의 refreshToken을 삭제
        if (redisUtil.getData(socialId) != null) {
            redisUtil.deleteData(socialId);
        }

        redisUtil.setDateExpire(accessToken, "logout", expiration);
    }
}
