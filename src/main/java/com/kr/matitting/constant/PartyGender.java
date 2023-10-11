package com.kr.matitting.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum PartyGender {
    ALL("all"), MALE("male"), FEMALE("female");

    @Getter
    private final String value;

    PartyGender(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PartyGender from(String value) {
        for (PartyGender status : PartyGender.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}