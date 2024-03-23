package com.kr.matitting.handler;

import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import static com.kr.matitting.exception.token.TokenExceptionType.VERIFICATION_ACCESS_TOKEN;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler implements ChannelInterceptor {
    //    private final ChatHandler chatHandler;
    private final JwtService jwtService;
//    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        try {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                String header = accessor.getFirstNativeHeader("Authorization");
                String token = header.replace("Bearer ", "");
                jwtService.isTokenValid(token);
            }
        } catch (Exception e) {
            log.error("토큰 유효성 검사 에러 발생 ", e);
            throw new TokenException(VERIFICATION_ACCESS_TOKEN);
        }
        return message;
    }
}