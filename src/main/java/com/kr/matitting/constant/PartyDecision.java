package com.kr.matitting.constant;

public enum PartyDecision {
    ACCEPT("ACCEPT"), REFUSE("REFUSE");

    PartyDecision(String key) {
        this.key = key;
    }

    private final String key;
}
