package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
@Schema(description = "no offset Response")
@Getter
public class ResponseNoOffsetDto {
    @Schema(description = "마지막 Id", nullable = false, example = "10")
    private Long lastId;
    @Schema(description = "다음 페이지 유무", nullable = false, example = "true")
    private boolean hasNext;

    public ResponseNoOffsetDto(Long lastId, boolean hasNext) {
        this.lastId = lastId;
        this.hasNext = hasNext;
    }
}