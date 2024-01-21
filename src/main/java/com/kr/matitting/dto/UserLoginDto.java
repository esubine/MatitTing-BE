package com.kr.matitting.dto;

import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import jakarta.validation.constraints.NotNull;

public record UserLoginDto(
        Long userId,
        Role role,
        String accessToken,
        String refreshToken
) {
}
