package com.kr.matitting.oauth2.service;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.UserLoginDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.oauth2.dto.OauthMember;
import com.kr.matitting.oauth2.dto.OauthParams;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@Component
public class OauthService {

    private final RequestOauthInfoService requestOauthInfoService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public final String[] NICKNAME = {"하늘", "구름", "숲속", "들판", "우주", "바다", "물결", "별빛", "소망", "노을",
            "노래", "햇살", "연목", "바람", "동산", "미지", "모험", "탐험"};
    public UserLoginDto getMemberByOauthLogin(OauthParams oauthParams) {
        log.debug("------- Oauth 로그인 시도 -------");

        //인증 파라미터 객체를 이용하여 해당 enum클래스에 해당하는 메소드 수행
        OauthMember request = requestOauthInfoService.request(oauthParams);
        log.debug("전달받은 유저정보:" + request.getEmail());

        //기존유저인지 신규유저인기 체크
        Optional<User> bySocialTypeAndSocialId = userRepository.findByOauthProviderAndSocialId(request.getOauthProvider(), request.getSocialId());

        //기존유저
        if (bySocialTypeAndSocialId.isPresent()) {
            User user = bySocialTypeAndSocialId.get();
            String accessToken = jwtService.createAccessToken(user);
            String refreshToken = jwtService.createRefreshToken(user);

            return new UserLoginDto(user.getId(), user.getRole(), accessToken, refreshToken);
        } 
        //신규유저
        else {
            Random random = new Random();
            User newUser = User.builder()
                    .email(request.getEmail())
                    .socialId(request.getSocialId())
                    .oauthProvider(request.getOauthProvider())
                    .nickname(NICKNAME[random.nextInt(18)] + random.nextInt(0, 999))
                    .age(-1)
                    .gender(Gender.UNKNOWN)
                    .role(Role.GUEST)
                    .build();
            User saved = userRepository.save(newUser);
            return new UserLoginDto(saved.getId(), saved.getRole(), null, null);
        }
    }
}
