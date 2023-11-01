package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.entity.User;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

public record UserSignUpDto(
        @NotNull
        String socialId,
        @NotNull
        SocialType socialType,
        @NotNull
        String email,
        @NotNull
        String nickname,
        @NotNull
        Integer age,
        String imgUrl,
        @NotNull
        Gender gender
) {
    public User toEntity() {
        return User.builder()
                .socialId(socialId)
                .socialType(socialType)
                .email(email)
                .nickname(nickname)
                .age(age)
                .imgUrl(imgUrl)
                .gender(gender)
                .role(Role.USER)
                .build();
    }
}
