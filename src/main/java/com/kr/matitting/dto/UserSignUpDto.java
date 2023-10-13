package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.entity.User;

import java.util.Optional;

public record UserSignUpDto(
        String socialId,
        SocialType socialType,
        String email,
        String nickname,
        Integer age,
        Optional<String> imgUrl,
        String city,
        Gender gender
) {
    public User toEntity() {
        return User.builder()
                .socialId(socialId)
                .socialType(socialType)
                .email(email)
                .nickname(nickname)
                .age(age)
                .imgUrl(imgUrl.get())
                .city(city)
                .gender(gender)
                .role(Role.USER)
                .build();
    }
}
