package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "내 채팅방 리스트 조회 Response")
public class ResponseChatRoomListDto {
    @Schema(description = "채팅방 List", type = "array", implementation = ResponseChatRoomDto.class)
    private List<ResponseChatRoomDto> responseChatRoomDtoList;
    @Schema(description = "채팅방의 페이지 정보", implementation = ResponseChatPageInfoDto.class)
    private ResponseChatPageInfoDto pageInfo;

}
