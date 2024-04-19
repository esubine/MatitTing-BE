package com.kr.matitting.handler;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.kr.matitting.exception.token.TokenExceptionType.*;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    // websocket을 통해 들어온 요청이 처리 되기전 실행됨
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
//        // websocket 연결시 헤더의 jwt token 유효성 검증

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);


        if (StompCommand.CONNECT == accessor.getCommand()) {
            String authorization = accessor.getFirstNativeHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                throw new TokenException(NOT_FOUND_ACCESS_TOKEN);
            }

            try {
                String token = authorization.replace("Bearer ", "");

                if (redisUtil.getData(token) == null) {
                    DecodedJWT decodedJWT = jwtService.isTokenValid(token);
                    String socialId = jwtService.getSocialId(decodedJWT);
                    User user = userRepository.findBySocialId(socialId).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
                    Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    log.error(BLACK_LIST_ACCESS_TOKEN.getErrorMessage());
                    throw new TokenException(BLACK_LIST_ACCESS_TOKEN);
                }
            } catch (TokenException e) {
                log.error("TokenException occurred: {}", e.getMessage(), e);
                throw new TokenException(INVALID_ACCESS_TOKEN);
            }
            return message;
        }
        return message;
    }
}
