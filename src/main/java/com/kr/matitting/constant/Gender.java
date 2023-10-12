package com.kr.matitting.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Gender {
    ALL("ALL"), MALE("MALE"), FEMALE("FEMALE");

    @Getter
    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Gender from(String value) {
        for (Gender status : Gender.values()) {
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