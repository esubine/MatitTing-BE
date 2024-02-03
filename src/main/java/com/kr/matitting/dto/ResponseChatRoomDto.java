package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.ChatUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 Response")
public class ResponseChatRoomDto {
    @Schema(description = "채팅방 id")
    private Long roomId;
    @Schema(description = "채팅방 제목")
    private String title;
    @Schema(description = "채팅방 마지막 전송시간")
    private LocalDateTime lastUpdate;

    public ResponseChatRoomDto(ChatRoom chatRoom) {
        this.roomId = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.lastUpdate = chatRoom.getModifiedDate();
    }

}
