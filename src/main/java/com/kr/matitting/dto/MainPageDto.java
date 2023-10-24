package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "Main Page DTO")
@Getter
public class MainPageDto {
    @Schema(description = "경도", nullable = false, example = "126.88453591058602")
    @NotNull
    Double longitude;
    @Schema(description = "위도", nullable = false, example = "37.53645109566274")
    @NotNull
    Double latitude;

    public MainPageDto(Double longitude, Double latitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
