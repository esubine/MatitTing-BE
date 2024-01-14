package com.kr.matitting.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ResponseRankingDto {
    @JsonProperty
    private String keyword;
}
