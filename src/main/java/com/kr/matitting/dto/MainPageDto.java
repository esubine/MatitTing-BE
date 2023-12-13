package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Sorts;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

@Schema(description = "Main Page DTO")
public record MainPageDto (
    @Schema(description = "경도", nullable = false, example = "126.88453591058602")
    Double longitude,
    @Schema(description = "위도", nullable = false, example = "37.53645109566274")
    Double latitude,
    @Schema(description = "파티상태 - 1. 입력하지 않는 경우(default): 모집 중인 파티만 조회 / 2. FINISH 입력 시: 모든 파티글 조회", nullable = true, example = "FINISH")
    PartyStatus partyStatus,
    @Schema(description = "조회할 갯수", nullable = true, example = "5")
    Integer size,
    @Schema(description = "마지막으로 조회한 파티 ID", nullable = true, example = "0")
    Long lastPartyId,
    @Schema(description = "정렬기준 - 1. 입력하지 않는 경우(default): 5km 반경의 파티글 중 유저와 가까운순 / 2. LATEST 입력 시: 5km 반경의 파티글 중 최신순 정렬", nullable = true, example = "LATEST")
    Sorts sort
){

    public MainPageDto(Double longitude, Double latitude, PartyStatus partyStatus, Integer size, Long lastPartyId, Sorts sort){
        this.longitude = longitude;
        this.latitude = latitude;
        this.partyStatus = partyStatus == null ? PartyStatus.RECRUIT : partyStatus;
        this.size = size == null ? 5 : size;
        this.lastPartyId = lastPartyId == null ? 0 : lastPartyId;
        this.sort = sort;
    }
}
