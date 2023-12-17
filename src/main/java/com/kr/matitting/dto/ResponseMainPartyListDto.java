package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.entity.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "메인 페이지 파티 리스트 Response")
public record ResponseMainPartyListDto(
        @Schema(description = "파티 id", nullable = false, example = "1")
        @NotNull
        Long partyId,
        @Schema(description = "파티 제목", nullable = false, example = "붕어빵 드실 분")
        @NotNull
        String partyTitle,
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
        @Schema(description = "파티 시작 시간", nullable = false, example = "2023-10-24T10:00:00")
        @NotNull
        LocalDateTime partyTime,
        @Schema(description = "모집 인원", nullable = false, example = "4")
        @NotNull
        Integer totalParticipate,
        @Schema(description = "현재 참가 인원", nullable = false, example = "2")
        @NotNull
        Integer participate,
        @Schema(description = "파티 메뉴", nullable = true, example = "붕어빵")
        String menu,
        @Schema(description = "카테고리", nullable = false, example = "한식")
        PartyCategory category,
        @Schema(description = "썸네일", nullable = false, example = " https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
        @NotNull
        String thumbnail
) {
    public static ResponseMainPartyListDto toDto(Party party) {
        return new ResponseMainPartyListDto(
                party.getId(),
                party.getPartyTitle(),
                getMainPageVersionAddress(party.getAddress()),
                party.getLongitude(),
                party.getLatitude(),
                party.getStatus(),
                party.getGender(),
                party.getAge(),
                party.getPartyTime(),
                party.getTotalParticipant(),
                party.getParticipantCount(),
                party.getMenu(),
                party.getCategory(),
                party.getThumbnail());
    }

    private static String getMainPageVersionAddress(String address){
        String addressList[] = address.split(" ");
        return addressList[0]+" "+addressList[1];
    }
}
