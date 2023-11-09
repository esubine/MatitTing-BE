package com.kr.matitting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kr.matitting.entity.ChatRoom;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public interface ChatRoomDto {

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    class ChatRoomResponseDto {
        private Long roomId;
        private String title;

        public static ChatRoomResponseDto of(ChatRoom chatRoom) {
            return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getTitle());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    class ChatRoomRequestDto {
        @NotNull
        @JsonProperty(value = "userId")
        private Long userId;

        public static ChatRoomResponseDto of(ChatRoom chatRoom) {
            return new ChatRoomResponseDto(chatRoom.getId(), chatRoom.getTitle());
        }
    }
}
