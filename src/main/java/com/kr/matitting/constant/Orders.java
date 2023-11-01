package com.kr.matitting.constant;

import lombok.Getter;

@Getter
public enum Orders {
    DESC("DESC"), ASC("ASC");

    Orders(String key) {
        this.key = key;
    }
    private final String key;
}