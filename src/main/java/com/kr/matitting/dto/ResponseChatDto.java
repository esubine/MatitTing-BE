package com.kr.matitting.dto;

import com.kr.matitting.constant.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseChatDto {
    private Long chatId;
    private Long senderId;
    private String nickname;
    private String message;
    private String imgUrl;
    private LocalDateTime createAt;
    private MessageType messageType;
}