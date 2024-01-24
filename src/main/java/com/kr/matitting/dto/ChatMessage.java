package com.kr.matitting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {
    private Long roomId;

    private Long chatUserId;

    private String message;
}
