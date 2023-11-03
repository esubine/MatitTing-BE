package com.kr.matitting.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.util.RedisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
@Transactional
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    /**
     * JWT의 Subject와 Claim으로 email 사용 -> 클레임의 name을 "email"으로 설정
     * JWT의 헤더에 들어오는 값 : 'Authorization(Key) = Bearer {토큰} (Value)' 형식
     */
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer ";
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(User user) {
        Date now = new Date();
        return JWT.create() //JWT 토큰 생성 빌더 반환
                .withSubject(ACCESS_TOKEN_SUBJECT) //JWT Subject 지정 -> AccessToken
                .withClaim("socialId", user.getSocialId())
                .withClaim("role", user.getRole().getKey())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(User user) {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withClaim("socialId", user.getSocialId())
                .withClaim("role", user.getRole().getKey())
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * Request Header token Get
     */
    public String extractToken(HttpServletRequest request, String tokenType) {
        Optional<String> token = null;

        if (tokenType == "accessToken") {
             token = Optional.ofNullable(request.getHeader(accessHeader))
                    .filter(refreshToken -> refreshToken.startsWith(BEARER))
                    .map(refreshToken -> refreshToken.replace(BEARER, ""));
            token.orElseThrow(() -> new TokenException(TokenExceptionType.NOT_FOUND_ACCESS_TOKEN));
        } else if (tokenType == "refreshToken") {
            token = Optional.ofNullable(request.getHeader(refreshHeader))
                    .filter(refreshToken -> refreshToken.startsWith(BEARER))
                    .map(refreshToken -> refreshToken.replace(BEARER, ""));
            token.orElseThrow(() -> new TokenException(TokenExceptionType.NOT_FOUND_REFRESH_TOKEN));
        }
        return token.get();
    }

    public void updateRefreshToken(String socialId, String refreshToken) {
        // Redis refreshToken
        redisUtil.setDateExpire(socialId, refreshToken, refreshTokenExpirationPeriod);
    }

    private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }
    private void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }
    public DecodedJWT isTokenValid(String token) {
            return JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
    }
    public String renewToken(String refreshToken) {
        //request refreshToken -> User SocialId를 get -> redis refreshToken 유효한지 찾아서 검사
        DecodedJWT decodedJWT = isTokenValid(refreshToken);
        String socialId = decodedJWT.getClaim("socialId").asString();
        String findToken = redisUtil.getData(socialId);

        if (findToken == null) {
            throw new TokenException(TokenExceptionType.NOT_FOUND_REFRESH_TOKEN);
        } else if (!findToken.equals(refreshToken)) {
            throw new TokenException(TokenExceptionType.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new TokenException(TokenExceptionType.INVALID_REFRESH_TOKEN));
        return createAccessToken(user);
    }

    public Long getExpiration(String accessToken) {
        // accessToken 남은 유효시간
        Date expiration = JWT
                .decode(accessToken)
                .getExpiresAt();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }


}
