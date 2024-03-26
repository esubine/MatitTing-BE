package com.kr.matitting.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static com.kr.matitting.exception.token.TokenExceptionType.VERIFICATION_ACCESS_TOKEN;

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
    private static final String BEARER = "Bearer ";
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    /**
     * AccessToken 생성 메소드
     */
    public String createAccessToken(User user) {
        checkRole(user);

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
        checkRole(user);

        Date now = new Date();
        String refreshToken = JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withClaim("socialId", user.getSocialId())
                .withClaim("role", user.getRole().getKey())
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
        updateRefreshToken(user, refreshToken);
        return refreshToken;
    }

    private void checkRole(User user) {
        if (user.getRole() == Role.GUEST)
            throw new UserException(UserExceptionType.INVALID_ROLE_USER);
    }

    /**
     * Request Header token Get
     */
    public String extractToken(HttpServletRequest request) {
        Optional<String> accessToken = extractTokenFromHeader(request, accessHeader);
        if (accessToken.isPresent()) {
            return accessToken.get();
        }

        Optional<String> refreshToken = extractTokenFromHeader(request, refreshHeader);
        if (refreshToken.isPresent()) {
            return refreshToken.get();
        }

        throw new TokenException(TokenExceptionType.NOT_FOUND_TOKEN);
    }

    private Optional<String> extractTokenFromHeader(HttpServletRequest request, String headerName) {
        return Optional.ofNullable(request.getHeader(headerName))
                .filter(header -> header.startsWith(BEARER))
                .map(header -> header.replace(BEARER, ""));
    }

    public void updateRefreshToken(User user, String refreshToken) {
        if (user.getRole() == Role.GUEST) {
            return;
        }
        // Redis refreshToken
        redisUtil.setDateExpire(user.getSocialId(), refreshToken, refreshTokenExpirationPeriod);
    }

    public DecodedJWT isTokenValid(String token) throws TokenExpiredException {
        try {
            return JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        } catch (JWTVerificationException e) {
            log.error(VERIFICATION_ACCESS_TOKEN.getErrorMessage());
            throw new TokenException(VERIFICATION_ACCESS_TOKEN);
        }
    }

    public String getSocialId(DecodedJWT decodedJWT) {
        return decodedJWT.getClaim("socialId").asString();
    }

    public String renewToken(String refreshToken) {
        DecodedJWT decodedJWT = isTokenValid(refreshToken);
        String socialId = decodedJWT.getClaim("socialId").asString();
        String findToken = redisUtil.getData(socialId);

        if (findToken == null || !findToken.equals(refreshToken))
            throw new TokenException(TokenExceptionType.NOT_FOUND_REFRESH_TOKEN);

        User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
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
