package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyJoinStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartyJoinDto {
    private Long partyId;

    private Long parentId;
    private Long userId;
    private PartyJoinStatus status;
    public PartyJoinDto() {
        this.status = PartyJoinStatus.WAIT;
    }

    public PartyJoinDto(Long partyId, Long parentId, Long userId) {
        this.partyId = partyId;
        this.parentId = parentId;
        this.userId = userId;
        this.status = PartyJoinStatus.WAIT;
    }

    public void Accept() {
        this.status = PartyJoinStatus.ACCEPT;
    }
    public void Refuse() {
        this.status = PartyJoinStatus.REFUSE;
    }
}
