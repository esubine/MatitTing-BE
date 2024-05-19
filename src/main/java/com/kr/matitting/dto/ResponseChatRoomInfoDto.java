package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Builder
@NoArgsConstructor
@Schema(description = "채팅방 정보 Response(채팅방 + 유저 리스트)")
public class ResponseChatRoomInfoDto {
    @Schema(description = "채팅방 Response")
    private ChatRoomInfoRes chatRoomInfoRes;
    @Schema(description = "채팅방 유저 리스트 Response")
    private ResponseChatUserList responseChatUserList;

    public ResponseChatRoomInfoDto(ChatRoomInfoRes chatRoomInfoRes, ResponseChatUserList responseChatUserList) {
        this.chatRoomInfoRes = chatRoomInfoRes;
        this.responseChatUserList = responseChatUserList;
    }
}