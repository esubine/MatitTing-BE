package com.kr.matitting.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final ChatHandler chatHandler;
    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        MessageHeaders headers = message.getHeaders();
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            log.info("total socket : {}", atomicInteger.incrementAndGet());
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
            String roomId = chatHandler.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            log.info("SUBSCRIBED {}", roomId);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            String roomId = chatHandler.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            log.info("total socket : {} / roomId = {}", atomicInteger.decrementAndGet(), roomId);
        }
        return message;
    }
}