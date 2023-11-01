package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;
public record UserUpdateDto (
    @Schema(description = "사용자 ID", nullable = false, example = "101")
    @NotNull
    Long userId,
    @Schema(description = "사용자 닉네임", nullable = true, example = "새싹개발자")
    String nickname,
    @Schema(description = "사용자 프로필 사진", nullable = true, example = "www.증명사진.jpg")
    String imgUrl){

}

