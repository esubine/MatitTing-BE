package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSearchPageDto {
    @Schema(description = "파티 리스트", nullable = true, example = "[{party1}, {party2} ..]")
    private List<ResponsePartyDto> partyList;
    @Schema(description = "메인 페이지의 페이지 정보", implementation = ResponsePageInfoDto.class)
    private ResponsePageInfoDto pageInfo;
}
