package com.kr.matitting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ResponseMyParty {
    private List<ResponsePartyDto> partyList;
    private ResponsePageInfoDto pageInfo;
}