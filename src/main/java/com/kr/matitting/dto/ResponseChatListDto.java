package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Schema(description = "채팅 기록 Response")
public class ResponseChatListDto {
    @Schema(description = "채팅 내역 List", type = "array", implementation = ResponseChatDto.class)
    private List<ResponseChatDto> responseChatDtoList;

    @Schema(description = "채팅의 페이지 정보", implementation = ResponsePageInfoDto.class)
    private ResponsePageInfoDto pageInfo;

    public ResponseChatListDto(List<ResponseChatDto> responseChatDtoList, ResponsePageInfoDto pageInfo) {
        this.responseChatDtoList = responseChatDtoList;
        this.pageInfo = pageInfo;
    }
}
