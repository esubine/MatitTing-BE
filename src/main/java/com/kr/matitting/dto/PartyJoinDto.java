package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record PartyJoinDto(
        @Schema(description = "파티 아이디", example = "13")
        @NotNull
        Long partyId,
        @Schema(description = "파티장 아이디", example = "1")
        @NotNull
        Long leaderId,
        @Schema(description = "파티 신청 수락/거절", example = "APPLY")
        @NotNull
        PartyJoinStatus status

) {
    public PartyJoinDto PartyJoinDto(Long partyId, Long leaderId, PartyJoinStatus status) {
        return new PartyJoinDto(partyId, leaderId, status);
    }
}
