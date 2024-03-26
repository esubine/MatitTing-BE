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
    @Schema(description = "요청한 page", example = "3")
    private Integer page;
    @Schema(description = "마지막 여부", example = "true or false")
    private Boolean hasNext;
}
