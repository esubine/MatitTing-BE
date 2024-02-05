package com.kr.matitting.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.ChatUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public interface ChatRoomDto {
    @Getter
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    class ChatResponse<T> {
        private Integer count;
        private T data;

        public ChatResponse(T data) {
            this.data = data;
            setCount(data);
        }

        public void setCount(Object data) {
            if(data instanceof List) {
                count = ((List) data).size();;
            }
        }
    }

    @Getter
    class ChatRoomItem {
        private Long roomId;
        private String title;
        private LocalDateTime lastUpdate;

        public ChatRoomItem(ChatUser chatUser) {
            this.roomId = chatUser.getChatRoom().getId();
            this.title = chatUser.getChatRoom().getTitle();
            this.lastUpdate = chatUser.getChatRoom().getModifiedDate();
        }

        public ChatRoomItem(ChatRoom chatRoom) {
            this.roomId = chatRoom.getId();
            this.title = chatRoom.getTitle();
            this.lastUpdate = chatRoom.getModifiedDate();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    class CreateRoomEvent {
        private Long partyId;
        private Long userId;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    class JoinRoomEvent {
        private Long partyId;
        private Long userId;
    }
}
