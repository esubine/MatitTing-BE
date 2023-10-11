package com.kr.matitting.constant;

import lombok.Getter;

@Getter
public enum PartyJoinStatus {
    //파티 참가 수락/대기/거절
    ACCEPT("ACCEPT"), WAIT("WAIT"), REFUSE("REFUSE");

    PartyJoinStatus(String key) {
        this.key = key;
    }
    private final String key;
}
