package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 정보 Response")
public class ChatRoomInfoRes {
    @Schema(description = "채팅방 id")
    private Long chatRoomId;
    @Schema(description = "채팅방 제목 id")
    private String title;
    @Schema(description = "채팅 방장 id")
    private Long masterUserId;
    @Schema(description = "채팅방에 해당하는 파티 id")
    private Long partyId;

    public ChatRoomInfoRes(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.masterUserId = chatRoom.getMaster().getId();
        this.partyId = chatRoom.getParty().getId();
    }
}
