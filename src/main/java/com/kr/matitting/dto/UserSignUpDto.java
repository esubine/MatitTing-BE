package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserSignUpDto(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,
        @Schema(description = "별명", example = "새싹개발자")
        @NotNull
        String nickname,
        @Schema(description = "나이", example = "26")
        @NotNull
        Integer age,
        @Schema(description = "성별", example = "MALE")
        @NotNull
        Gender gender
) {
    public User toEntity() {
        return User.builder()
                .id(userId)
                .nickname(nickname)
                .age(age)
                .gender(gender)
                .role(Role.USER)
                .build();
    }
}
