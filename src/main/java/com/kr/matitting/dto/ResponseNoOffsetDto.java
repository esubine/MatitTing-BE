package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "no offset Response")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseNoOffsetDto {
    @Schema(description = "마지막 Id", example = "10")
    private Long lastId;
    @Schema(description = "다음 페이지 유무", example = "true")
    private boolean hasNext;
}