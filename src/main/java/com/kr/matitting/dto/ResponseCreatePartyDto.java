package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "파티 생성 Response")
public class ResponseCreatePartyDto {
    @Schema(description = "파티 id")
    private Long partyId;
    @Schema(description = "채팅방 id")
    private Long chatRoomId;

    public ResponseCreatePartyDto(Party party, ChatRoom chatRoom) {
        this.partyId = party.getId();
        this.chatRoomId = chatRoom.getId();
    }
}
