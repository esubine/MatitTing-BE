package com.kr.matitting.constant;

import lombok.Getter;

@Getter
public enum Sorts {
    HIT("HIT"), DEADLINE("DEADLINE"), LATEST("LATEST");

    Sorts(String key) {
        this.key = key;
    }
    private final String key;
}