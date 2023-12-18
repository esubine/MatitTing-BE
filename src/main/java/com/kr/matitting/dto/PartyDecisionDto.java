package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyDecision;
import com.kr.matitting.constant.PartyJoinStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyDecisionDto {
    @Schema(description = "파티 아이디", example = "13")
    @NotNull
    private Long partyId;
    @Schema(description = "참가 요청자 유저 닉네임", example = "새싹개발자")
    private String nickname;
    @Schema(description = "파티 신청 수락/거절", example = "ACCEPT")
    @NotNull
    private PartyDecision status;

}
