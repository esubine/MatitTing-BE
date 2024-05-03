package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 유저 리스트 조회 Response")
public class ResponseChatUserList {
    private List<ResponseChatRoomUserDto> chatRoomUserDto;
    private ResponseMyChatUserInfo myInfo;

}
