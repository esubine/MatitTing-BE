package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseChatRoomInfoDto {
    private Long chatRoomId;
    private String title;
    private Long masterId;
    private Long partyId;

    public ResponseChatRoomInfoDto(ChatRoom chatRoom) {
        this.chatRoomId = chatRoom.getId();
        this.title = chatRoom.getTitle();
        this.masterId = chatRoom.getMaster().getId();
        this.partyId = chatRoom.getParty().getId();
    }
}