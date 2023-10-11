package com.kr.matitting.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum PartyCategory {
    KOREAN("한식"), WESTERN("양식"), JAPANESE("일식"), CHINESE("중식"), ETC("기타");

    @Getter
    private final String value;

    PartyCategory(String value) {
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