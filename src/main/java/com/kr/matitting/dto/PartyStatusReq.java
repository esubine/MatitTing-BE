package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartyStatusReq {
    @Schema(description = "유저의 Role에 따라서 방장으로 속한 파티 or 참여하고 있는 파티를 Response 해주는 Column", example = "HOST or VOLUNTEER")
    @NotNull
    private Role role;
    @Schema(description = "파티 상태 Filter, 모집중 or 모집완료 or 파티종료", example = "RECRUIT or RECRUIT_FINISH or PARTY_FINISH")
    private PartyStatus status = PartyStatus.RECRUIT;
}