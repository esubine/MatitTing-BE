package com.kr.matitting.constant;

import lombok.Getter;

@Getter
public enum Role {
    GUEST("ROLE_GUEST"), USER("ROLE_USER"),
    VOLUNTEER("ROLE_VOLUNTEER"), HOST("ROLE_HOST");
    Role(String key) {
        this.key = key;
    }
    private final String key;
}
