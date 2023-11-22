package com.kr.matitting.jwt.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.oauth2.CustomOauth2User;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import static com.kr.matitting.exception.token.TokenExceptionType.*;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    @Value("${jwt.secret}")
    private String secretKey;

    private static final String[] whitelist = {"/", "/index.html", "/home", "/login", "/oauth2/**",
            "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**","/demo-ui.html", "/swagger-ui/**", "/api-docs/**",
            "/api/chat-rooms/**", "/chat/**", "/room/**", "/webjars/**", "/favicon.ico", "/ws-stomp/**"};


    // 필터를 거치지 않을 URL 을 설정하고, true 를 return 하면 바로 다음 필터를 진행하게 됨
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(whitelist, request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String header = request.getRequestURI();
        // 토큰이 없거나 정상적이지 않은 경우
        if (header == null || !header.startsWith("Bearer ")) {
            log.error(NOT_FOUND_ACCESS_TOKEN.getErrorMessage());
            throw new TokenException(NOT_FOUND_ACCESS_TOKEN);
        }

        try {
            // 토큰 검증
            String token = header.replace("Bearer ", "");
            if (redisUtil.getData(token) == null) { //redis blacklist check
                DecodedJWT decodedJWT = jwtService.isTokenValid(token);
                String socialId = jwtService.getSocialId(decodedJWT);
                User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                doFilter(request, response, filterChain);
            } else {
                log.error(BLACK_LIST_ACCESS_TOKEN.getErrorMessage());
                throw new TokenException(BLACK_LIST_ACCESS_TOKEN);
            }
        } catch (TokenExpiredException e) {
            log.error(INVALID_ACCESS_TOKEN.getErrorMessage());
            throw new TokenException(INVALID_ACCESS_TOKEN);
        } catch (Exception e) {
            log.error(INVALID_ACCESS_TOKEN.getErrorMessage());
            throw new TokenException(INVALID_ACCESS_TOKEN);
        }
    }
}