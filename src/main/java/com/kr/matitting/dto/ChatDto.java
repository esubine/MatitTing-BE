package com.kr.matitting.dto;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.entity.ChatUser;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public interface ChatDto {
    @Getter
    @AllArgsConstructor
    class CreateRoomEvent {
        private Party party;
        private User owner;
    }

    @Getter
    class CreatePrivateRoomEvent {
        private Party party;
        private User owner;
        private User participant;
    }

    @Getter
    class ChatMessage {
        public enum MessageType {
            ENTER, TALK, OUT,
        }

        private MessageType type;
        private Long roomId;
        private String sender;
        private String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
