package com.kr.matitting.dto;

import lombok.Getter;

@Getter
public class CalculateDto {
    double minLatitude;
    double maxLatitude;
    double minLongitude;
    double maxLongitude;

    public CalculateDto(double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        this.minLatitude = minLatitude;
        this.maxLatitude = maxLatitude;
        this.minLongitude = minLongitude;
        this.maxLongitude = maxLongitude;
    }
}
