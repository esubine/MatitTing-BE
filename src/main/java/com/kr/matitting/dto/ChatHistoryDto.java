package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatHistory;
import com.kr.matitting.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

public interface ChatHistoryDto {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class ChatHistoryResponseDto {
        private Long senderId;
        private String content;
        private LocalDateTime sendTime;

        public static ChatHistoryResponseDto of(ChatHistory chatHistory) {
            return new ChatHistoryResponseDto(chatHistory.getSender().getId(), chatHistory.getContent(), chatHistory.getCreateDate());
        }
    }
}
