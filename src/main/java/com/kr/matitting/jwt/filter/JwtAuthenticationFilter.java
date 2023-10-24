package com.kr.matitting.jwt.filter;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.util.RedisUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;


@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    @Value("${jwt.secret}")
    private String secretKey;

    private static final String[] whitelist = {"/", "/index.html", "/home", "/login", "/oauth2/**",
            "/login/oauth2/code/**", "/oauth2/signUp", "/error", "/js/**","/demo-ui.html", "/swagger-ui/", "/api-docs/"};

    // 필터를 거치지 않을 URL 을 설정하고, true 를 return 하면 바로 다음 필터를 진행하게 됨
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        String header = request.getHeader("Authorization");

        // 토큰이 없거나 정상적이지 않은 경우
        if (header == null || !header.startsWith("Bearer ")) {
            throw new TokenException(TokenExceptionType.NOT_FOUND_ACCESS_TOKEN);
        }

        try {
            // 토큰 검증
            String token = header.replace("BEARER ", "");
            if (redisUtil.getData(token) == null) { //redis blacklist check
                DecodedJWT decodedJWT = jwtService.isTokenValid(token);

                String socialId = decodedJWT.getClaim("socialId").asString();
                String role = decodedJWT.getClaim("role").asString();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(socialId, null, Collections.singleton(new SimpleGrantedAuthority(role)));

                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                doFilter(request, response, filterChain);
            } else {
                throw new TokenException(TokenExceptionType.BLACK_LIST_ACCESS_TOKEN);
            }
        } catch (TokenExpiredException e) {
            throw new TokenException(TokenExceptionType.INVALID_ACCESS_TOKEN);
        } catch (Exception e) {
            throw new TokenException(TokenExceptionType.INVALID_ACCESS_TOKEN);
        }
    }
}