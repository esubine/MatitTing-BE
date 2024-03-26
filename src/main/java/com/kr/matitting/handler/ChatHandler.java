//package com.kr.matitting.handler;
//
//import com.kr.matitting.constant.MessageType;
//import com.kr.matitting.dto.ChatMessageDto;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.stereotype.Component;
//
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class ChatHandler {
//    private final RedisTemplate redisTemplate;
//    private final ChannelTopic channelTopic;
//    public String getRoomId(String destination) {
//        int lastIndex = destination.lastIndexOf('/');
//        if (lastIndex != -1)
//            return destination.substring(lastIndex + 1);
//        else
//            return "";
//    }
//
//    public void sendChatMessage(ChatMessageDto chatMessageDto) {
//        if (MessageType.ENTER.equals(chatMessageDto.getType())) {
//            chatMessageDto.setMessage(chatMessageDto.getChatUserId() + "님이 방에 입장했습니다.");
////        } else if (MessageType.EXIT.equals(chatMessageDto.getType())) {
////            chatMessageDto.setMessage(chatMessageDto.getChatUserId() + "님이 방에서 나갔습니다.");
////            chatMessage.setSender("[알림]");
//        }
//        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessageDto);
//    }
//}
