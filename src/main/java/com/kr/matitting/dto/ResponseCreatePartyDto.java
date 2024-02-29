package com.kr.matitting.dto;

import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.Party;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseCreatePartyDto {
    private Long partyId;
    private Long chatRoomId;

    public ResponseCreatePartyDto(Party party, ChatRoom chatRoom) {
        this.partyId = party.getId();
        this.chatRoomId = chatRoom.getId();
    }
}
