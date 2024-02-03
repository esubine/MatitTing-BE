package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "채팅 메시지 객체")
public class ChatMessage {
    private Long roomId;

    private Long chatUserId;

    private String message;
}
