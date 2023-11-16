package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.entity.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "파티 Response")
public record ResponsePartyDto(
        @Schema(description = "파티 제목", nullable = false, example = "붕어빵 드실 분")
        @NotNull
        String partyTitle,
        @Schema(description = "파티 내용", nullable = false, example = "붕어빵은 팥이 근본입니다.")
        @NotNull
        String partyContent,
        @Schema(description = "주소", nullable = false, example = "서울 송파구 송파동 7-1")
        @NotNull
        String address,
        @Schema(description = "경도", nullable = false, example = "126.88453591058602")
        @NotNull
        double longitude,
        @Schema(description = "위도", nullable = false, example = "37.53645109566274")
        @NotNull
        double latitude,
        @Schema(description = "파티 상태", nullable = false, example = "RECRUIT")
        @NotNull
        PartyStatus status,
        @Schema(description = "성별", nullable = false, example = "ALL")
        @NotNull
        Gender gender,
        @Schema(description = "연령대", nullable = false, example = "2030")
        @NotNull
        PartyAge age,
        @Schema(description = "파티 모집 마감 시간", nullable = false, example = "2023-10-24T09:00:00")
        @NotNull
        LocalDateTime deadline,
        @Schema(description = "파티 시작 시간", nullable = false, example = "2023-10-24T10:00:00")
        @NotNull
        LocalDateTime partyTime,
        @Schema(description = "모집 인원", nullable = false, example = "4")
        @NotNull
        Integer totalParticipate,
        @Schema(description = "현재 참가 인원", nullable = false, example = "2")
        @NotNull
        Integer participate,
        @Schema(description = "파티 메뉴", nullable = true, example = "돈까스")
        String menu,
        @Schema(description = "썸네일", nullable = true, example = " https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
        @NotNull
        String thumbnail,

        @Schema(description = "조회수", nullable = false, example = "7")
        @NotNull
        int hit
) {
    public static ResponsePartyDto toDto(Party party) {
        return new ResponsePartyDto(
                party.getPartyTitle(),
                party.getPartyContent(),
                party.getAddress(),
                party.getLongitude(),
                party.getLatitude(),
                party.getStatus(),
                party.getGender(),
                party.getAge(),
                party.getDeadline(),
                party.getPartyTime(),
                party.getTotalParticipant(),
                party.getParticipantCount(),
                party.getMenu(),
                party.getThumbnail(),
                party.getHit());
    }
}
