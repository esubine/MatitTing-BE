package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
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
    @NotNull
    private Role role;
    private PartyStatus status = PartyStatus.RECRUIT;
}