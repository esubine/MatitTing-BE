package com.kr.matitting.dto;

public record ResponseRankingDto(
        String keyword,
        Double score
) {
    public ResponseRankingDto(String keyword, Double score) {
        this.keyword = keyword;
        this.score = score;
    }
}
