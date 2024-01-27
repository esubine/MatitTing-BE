package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserSignUpDto(
        @Schema(description = "소셜 아이디", example = "123123")
        @NotNull
        String socialId,
        @Schema(description = "소셜 타입", example = "NAVER")
        @NotNull
        OauthProvider oauthProvider,
        @Schema(description = "이메일", example = "rewrw123@naver.com")
        @NotNull
        String email,
        @Schema(description = "별명", example = "새싹개발자")
        @NotNull
        String nickname,
        @Schema(description = "나이", example = "26")
        @NotNull
        Integer age,
        @Schema(description = "프로필 사진", example = "증명사진.jpg")
        String imgUrl,
        @Schema(description = "성별", example = "MALE")
        @NotNull
        Gender gender
) {
    public User toEntity() {
        return User.builder()
                .socialId(socialId)
                .oauthProvider(oauthProvider)
                .email(email)
                .nickname(nickname)
                .age(age)
                .imgUrl(imgUrl)
                .gender(gender)
                .role(Role.USER)
                .build();
    }
}
