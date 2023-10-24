package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ResponseRankingDto(
        @Schema(description = "인기 검색어", nullable = false, example = "돈까스")
        String keyword,
        @Schema(description = "검색 횟수", nullable = false, example = "2")
        Double score
) {
    public ResponseRankingDto(String keyword, Double score) {
        this.keyword = keyword;
        this.score = score;
    }
}
