package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseLoginDto {
    @Schema(description = "신규 유저일 경우 해당 유저의 ID를 return, 기존 유저일 경우 null을 return", nullable = true, example = "{id} or null")
    private Long newUserId;
}
