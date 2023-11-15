package com.kr.matitting.handler;

import com.kr.matitting.dto.ChatHistoryDto;
import com.kr.matitting.dto.ChatUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import static com.kr.matitting.dto.ChatUserDto.*;


@Component
@Slf4j
@RequiredArgsConstructor
public class ChatHandler {
    private final RedisTemplate redisTemplate;
    private final ChannelTopic channelTopic;
    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }

    public void sendChatMessage(ChatHistoryDto.ChatMessage chatMessage) {
//        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
//            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
//            chatMessage.setSender("[알림]");
//        } else if (ChatMessage.MessageType.OUT.equals(chatMessage.getType())) {
//            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
//            chatMessage.setSender("[알림]");
//        }
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
