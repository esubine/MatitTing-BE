package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Optional;

public record PartyUpdateDto(
        @Schema(description = "파티제목", nullable = true, example = "같이 밥먹을사람~!")
        String partyTitle,
        @Schema(description = "파티 모집 글", nullable = true, example = "행궁동에서 파스타 먹을사람 모집중!")
        String partyContent,
        @Schema(description = "메뉴", nullable = true, example = "돈까스")
        String menu,
        @Schema(description = "장소 위도", nullable = true, example = "127.001")
        Double longitude,
        @Schema(description = "장소 경도", nullable = true, example = "37.001")
        Double latitude,
        @Schema(description = "파티 장소", nullable = true, example = "달달 블라썸")
        String partyPlaceName,
        @Schema(description = "모집 여부", nullable = true, example = "RECRUIT")
        PartyStatus status, //모집 여부
        @Schema(description = "모집 인원", nullable = true, example = "4")
        Integer totalParticipant, //모집 인원
        @Schema(description = "모집 성별", nullable = true, example = "ALL")
        Gender gender, //성별
        @Schema(description = "모집 나이", nullable = true, example = "AGE2030")
        PartyAge age,
        @Schema(description = "썸네일", nullable = true, example = "돈까스.jpg")
        String thumbnail, //썸네일
        @Schema(description = "파티 시작시간", nullable = true, example = "2021-12-08T11:44:30.327959")
        LocalDateTime partyTime

) {
}
