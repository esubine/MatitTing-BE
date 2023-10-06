package com.kr.matitting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseRankingDto {
    private String keyword;
    private Double score;
}
