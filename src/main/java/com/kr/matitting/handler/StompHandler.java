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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;

import static com.kr.matitting.exception.token.TokenExceptionType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final JwtService jwtService;
    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if (StompCommand.CONNECT == accessor.getCommand()) {
                String header = accessor.getFirstNativeHeader("Authorization");

                if (header == null || !header.startsWith("Bearer ")) {
                    throw new TokenException(NOT_FOUND_ACCESS_TOKEN);
                }
                String token = header.replace("Bearer ", "");

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
            }
        } catch (Exception e) {
            log.error(INVALID_ACCESS_TOKEN.getErrorMessage());
            throw new TokenException(INVALID_ACCESS_TOKEN);
        }
        return message;
    }
}