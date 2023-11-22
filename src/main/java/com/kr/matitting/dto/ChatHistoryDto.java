package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatHistory;
import lombok.Getter;

import java.time.LocalDateTime;

public interface ChatHistoryDto {
    @Getter
    class ChatHistoryResponse {
        private Long chatUserId;
        private String nickname;
        private String content;
        private LocalDateTime createAt;

        public ChatHistoryResponse(ChatHistory chatHistory) {
            this.chatUserId = chatHistory.getChatUser().getId();
            this.nickname = chatHistory.getChatUser().getNickname();
            this.content = chatHistory.getContent();
            this.createAt = chatHistory.getCreateDate();
        }
    }

    @Getter
    class ChatMessage {
        private Long roomId;
        private Long chatUserId;
        private String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
