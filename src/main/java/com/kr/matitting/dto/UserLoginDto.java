package com.kr.matitting.dto;

import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import jakarta.validation.constraints.NotNull;

public record UserLoginDto(
        @NotNull
        String email,
        @NotNull
        SocialType socialType,
        @NotNull
        String socialId,
        @NotNull
        Role role,
        String accessToken,
        String refreshToken
) {
}
