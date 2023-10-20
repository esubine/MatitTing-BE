package com.kr.matitting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MainPageDto {
    @NotNull
    double longitude;
    @NotNull
    double latitude;
}
