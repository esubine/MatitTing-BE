package com.kr.matitting.dto;

import com.kr.matitting.constant.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResponseUserDto {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;
    @Schema(description = "사용자 role", nullable = false, example = "USER")
    private Role role; //신규유저 or 기존유저
}