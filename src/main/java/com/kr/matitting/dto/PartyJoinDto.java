package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyJoinStatus;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record PartyJoinDto(
        @NotNull
        Long partyId,
        @NotNull
        Long leaderId,
        @NotNull
        Long userId,
        @NotNull
        PartyJoinStatus status

) {
    public PartyJoinDto PartyJoinDto(Long partyId, Long leaderId, Long userId, PartyJoinStatus status) {
        return new PartyJoinDto(partyId, leaderId, userId, (status == null) ? PartyJoinStatus.WAIT : status);
    }
}
