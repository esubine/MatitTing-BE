package com.kr.matitting.oauth2.handler;

import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.oauth2.CustomOauth2User;
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

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("OAuth2 Login 성공!");
        User user = ((CustomOauth2User) authentication.getPrincipal()).getUser();

        String redirectURL = "";
        UriComponentsBuilder redirectURLBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/login")
                .queryParam("email", user.getEmail())
                .queryParam("socialType", user.getSocialType())
                .queryParam("socialId", user.getSocialId())
                .queryParam("role", user.getRole());

        if (user.getRole().equals(Role.GUEST)) {
            redirectURL = redirectURLBuilder.build().encode(StandardCharsets.UTF_8).toUriString();
        } else if (user.getRole().equals(Role.USER)) {
            String accessToken = jwtService.createAccessToken(user);
            String refreshToken = jwtService.createRefreshToken(user);
            jwtService.updateRefreshToken(user.getSocialId(), refreshToken);

            redirectURL = redirectURLBuilder
                    .queryParam("accessToken", "Bearer " + accessToken)
                    .queryParam("refreshToken", "Bearer " + refreshToken)
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        }
        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}
