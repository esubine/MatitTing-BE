package com.kr.matitting.handler;

import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static com.kr.matitting.exception.token.TokenExceptionType.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final JwtService jwtService;
    private final RedisUtil redisUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String header = accessor.getFirstNativeHeader("Authorization");

                if (header == null || !header.startsWith("Bearer ")) {
                    throw new TokenException(NOT_FOUND_ACCESS_TOKEN);
                }
                String token = header.replace("Bearer ", "");

                if (redisUtil.getData(token) == null) {
                    jwtService.isTokenValid(token);
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