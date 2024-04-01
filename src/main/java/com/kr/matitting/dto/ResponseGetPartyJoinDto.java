package com.kr.matitting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseGetPartyJoinDto {
    private List<InvitationRequestDto> partyList;
    private ResponsePageInfoDto pageInfo;
}