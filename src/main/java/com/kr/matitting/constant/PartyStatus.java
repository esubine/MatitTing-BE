package com.kr.matitting.constant;

import lombok.Getter;

@Getter
public enum PartyStatus {
    //모집 여부
    RECRUIT("RECRUIT"), RECRUIT_FINISH("RECRUIT_FINISH"), PARTY_FINISH("PARTY_FINISH");
    PartyStatus(String key) {
        this.key = key;
    }
    private final String key;
}
