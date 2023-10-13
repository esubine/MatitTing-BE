package com.kr.matitting.constant;

import lombok.Getter;

@Getter
public enum PartyStatus {
    //모집 여부
    RECRUIT("RECRUIT"), PARTICIPATE("PARTICIPATE"), FINISH("FINISH");
    PartyStatus(String key) {
        this.key = key;
    }
    private final String key;
}
