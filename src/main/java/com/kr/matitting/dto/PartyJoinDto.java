package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record PartyJoinDto(
        @Schema(description = "파티 아이디", example = "13")
        @NotNull
        Long partyId,
        @Schema(description = "파티 신청 수락/거절", example = "APPLY")
        @NotNull
        PartyJoinStatus status,
        @Schema(description = "한줄 소개", example = "안녕하세요 저는 밝고 활발한 성격을 가진 27세 남자입니다.")
        String oneLineIntroduce
) {
    public PartyJoinDto PartyJoinDto(Long partyId, PartyJoinStatus status, String oneLineIntroduce) {
        return new PartyJoinDto(partyId, status, oneLineIntroduce);
    }
}
