package com.kr.matitting.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    //TODO: ROLE_ 쓰는 이유 Study
    GUEST("ROLE_GUEST"), USER("ROLE_USER"),

    VOLUNTEER("ROLE_VOLUNTEER"), HOST("ROLE_HOST");

    private final String key;
}
