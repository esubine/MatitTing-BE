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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        
        User user = ((CustomOauth2User) authentication.getPrincipal()).getUser();
        String accessToken = jwtService.createAccessToken(user);
        

        
        if (user.getRole().equals(Role.GUEST)) { //신규 유저 -> signUp으로 email, socialType, socialId를 send
            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            String redirectURL = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/signUp")
                    .queryParam("email", user.getEmail())
                    .queryParam("socialType", user.getSocialType())
                    .queryParam("socialId", user.getSocialId())
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, redirectURL);
        } else { //기존 유저 -> refreshToken을 발급하고 update, response에 accessToken, refreshToken을 담아서 send
            String refreshToken = jwtService.createRefreshToken(user);
            jwtService.updateRefreshToken(user.getSocialId(), refreshToken);

            response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
            response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

            // 최초 로그인이 아닌 경우 로그인 성공 페이지로 이동
            String redirectURL = UriComponentsBuilder.fromUriString("http://localhost:8080/loginSuccess")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
            getRedirectStrategy().sendRedirect(request, response, redirectURL);
        }
    }
}
