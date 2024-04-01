package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponsePartyJoinDto {
    @Schema(description = "파티 신청 ID", example = "1")
    private Long partyJoinId;
}
