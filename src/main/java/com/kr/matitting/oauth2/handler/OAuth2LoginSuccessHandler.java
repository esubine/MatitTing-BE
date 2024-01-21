package com.kr.matitting.oauth2.handler;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.oauth2.CustomOauth2User;
import com.kr.matitting.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    public final String[] NICKNAME = {"하늘", "구름", "숲속", "들판", "우주", "바다", "물결", "별빛", "소망", "노을",
            "노래", "햇살", "연목", "바람", "동산", "미지", "모험", "탐험"};

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 Login 성공!");
        User user = ((CustomOauth2User) authentication.getPrincipal()).getUser();
        User responseUser;

        Random random = new Random();
        if (user.getRole().equals(Role.GUEST)) {
            User newUser = User.builder()
                    .email(user.getEmail())
                    .socialId(user.getSocialId())
                    .socialType(user.getSocialType())
                    .nickname(NICKNAME[random.nextInt(18)] + random.nextInt(0, 999))
                    .age(-1)
                    .gender(Gender.UNKNOWN)
                    .role(user.getRole())
                    .build();
            if (userRepository.findBySocialId(newUser.getSocialId()).isPresent()) {
                throw new UserException(UserExceptionType.DUPLICATION_USER);
            }
            responseUser = userRepository.save(newUser);
        } else {
            responseUser = userRepository.findBySocialId(user.getSocialId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        }

        String accessToken = jwtService.createAccessToken(responseUser);
        String refreshToken = jwtService.createRefreshToken(responseUser);
        jwtService.updateRefreshToken(user, refreshToken);

        String redirectURL = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/login")
                .queryParam("userId", responseUser.getId())
                .queryParam("role", responseUser.getRole())
                .queryParam("accessToken", accessToken == null ? null : "Bearer " + accessToken)
                .queryParam("refreshToken", refreshToken == null ? null :"Bearer " + refreshToken)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}
