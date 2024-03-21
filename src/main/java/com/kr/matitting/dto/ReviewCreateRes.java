package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewCreateRes {
    @Schema(description = "작성된 리뷰 ID", example = "200")
    private Long reviewId;
}
