package com.kr.matitting.dto;

import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.ChatUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅방 내 유저정보 조회 Response")
public class ResponseChatRoomUserDto {
    @Schema(description = "채팅유저 id")
    private Long chatUserId;
    @Schema(description = "채팅방 내 역할", example = "HOST")
    private Role role;
    @Schema(description = "채팅유저의 닉네임")
    private String nickname;

    public ResponseChatRoomUserDto(ChatUser chatUser) {
        this.chatUserId = chatUser.getId();
        this.role = chatUser.getUserRole();
        this.nickname = chatUser.getNickname();
    }
}
