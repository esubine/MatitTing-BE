package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Sorts;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Main Page DTO")
public class MainPageDto {
    @Schema(description = "경도", nullable = false, example = "126.88453591058602")
    @NotNull
    private Double longitude;

    @Schema(description = "위도", nullable = false, example = "37.53645109566274")
    @NotNull
    private Double latitude;

    @Schema(description = "파티상태 - 1. 입력하지 않는 경우(default): 모집 중인 파티만 조회 / 2. FINISH 입력 시: 모든 파티글 조회", nullable = true, example = "FINISH")
    private PartyStatus partyStatus;

    @Schema(description = "정렬기준 - 1. 입력하지 않는 경우(default): 5km 반경의 파티글 중 유저와 가까운순 / 2. LATEST 입력 시: 5km 반경의 파티글 중 최신순 정렬", nullable = true, example = "LATEST")
    private Sorts sort;

    public MainPageDto() {
        // 기본값 설정
        this.longitude = 126.978646598009; // 기본 경도
        this.latitude = 37.566828706631135; // 기본 위도
        this.partyStatus = PartyStatus.RECRUIT; // 기본 파티 상태
    }
}
