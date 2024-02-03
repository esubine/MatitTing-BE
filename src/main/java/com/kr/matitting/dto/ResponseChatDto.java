package com.kr.matitting.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseChatDto {
    private Long senderId;
    private String nickname;
    private String message;
    private LocalDateTime createAt;


}
