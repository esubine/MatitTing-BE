package com.kr.matitting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSearchPageDto {
    private List<ResponsePartyDto> partyList;
    private Long lastPartyId;
    private Boolean hasNext;
}
