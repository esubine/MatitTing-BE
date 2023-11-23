package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Schema(description = "메인페이지 Response")
@Getter
public class ResponseMainPageDto {
    @Schema(description = "메인 페이지 party List", type = "array", implementation = ResponsePartyDto.class)
    private List<ResponsePartyDto> partyList;
    @Schema(description = "메인 페이지의 페이지 정보", implementation = ResponsePageInfoDto.class)
    private ResponsePageInfoDto pageInfo;

    public ResponseMainPageDto(List<ResponsePartyDto> responsePartyDto, ResponsePageInfoDto pageInfo) {
        this.partyList = responsePartyDto;
        this.pageInfo = pageInfo;
    }
}