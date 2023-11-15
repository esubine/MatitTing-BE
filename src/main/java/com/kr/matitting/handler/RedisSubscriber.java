package com.kr.matitting.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.matitting.dto.ChatHistoryDto;
import com.kr.matitting.dto.ChatUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
//@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;

    public void sendMessage(String publishMessage) {
        try {
            ChatHistoryDto.ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatHistoryDto.ChatMessage.class);
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}
