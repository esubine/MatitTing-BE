package com.kr.matitting.dto;

import com.kr.matitting.constant.Role;

public record UserLoginDto(
        Long userId,
        Role role,
        String accessToken,
        String refreshToken
) {
}
