package com.kr.matitting.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum PartyAge {
    AGE2030("2030"), AGE3040("3040"), AGE40("40"), ALL("ALL");

    @Getter
    private final String value;

    PartyAge(String value) {
        this.value = value;
    }

    @JsonCreator
    public static PartyCategory from(String value) {
        for (PartyCategory status : PartyCategory.values()) {
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