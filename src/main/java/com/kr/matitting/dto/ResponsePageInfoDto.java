package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Schema(description = "채팅 관련 페이지 정보 Response")
@Getter
public class ResponsePageInfoDto {
    @Schema(description = "이전에 조회한 page", nullable = false, example = "10")
    private Integer page;
    @Schema(description = "다음 페이지 유무", nullable = false, example = "true")
    private boolean hasNext;

    public ResponsePageInfoDto(Integer page, boolean hasNext) {
        this.page = page;
        this.hasNext = hasNext;
    }
}
