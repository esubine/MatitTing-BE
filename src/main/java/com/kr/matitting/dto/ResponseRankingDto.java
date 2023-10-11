package com.kr.matitting.dto;

import lombok.Data;

@Data
public class ResponseRankingDto {
    private String keyword;
    private Double score;
    public ResponseRankingDto(String keyword, Double score) {
        this.keyword = keyword;
        this.score = score;
    }
}
