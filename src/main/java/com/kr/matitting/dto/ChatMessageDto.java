package com.kr.matitting.dto;

import com.kr.matitting.constant.MessageType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "채팅 메시지 객체")
public class ChatMessageDto {
    private MessageType type;

    private Long roomId;

    private Long chatUserId;

    private String message;

}
