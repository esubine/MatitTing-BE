package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.entity.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "파티 생성 DTO")
public class PartyCreateDto {

    @Schema(description = "파티 제목", nullable = false, example = "붕어빵 먹을 사람")
    @NotNull
    private String partyTitle;
    @Schema(description = "파티 내용", nullable = false, example = "붕어빵은 팥이 근본입니다.")
    @NotNull
    private String partyContent;

    @Schema(description = "파티 장소명", nullable = false, example = "원조 붕어빵")
    @NotNull
    private String partyPlaceName;
    @Schema(description = "파티 시작 시간", nullable = false, example = "2023-10-24T10:00:00")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime partyTime;
    @Schema(description = "모집 인원", nullable = false, example = "4")
    @NotNull
    private int totalParticipant;
    @Schema(description = "경도", nullable = false, example = "126.88453591058602")
    @NotNull
    private double longitude;
    @Schema(description = "위도", nullable = false, example = "37.53645109566274")
    @NotNull
    private double latitude;
    @Schema(description = "성별", nullable = false, example = "ALL")
    @NotNull
    private Gender gender;
    @Schema(description = "카테고리", nullable = false, example = "한식")
    @NotNull
    private PartyCategory category;
    @Schema(description = "연령대", nullable = false, example = "2030")
    @NotNull
    private PartyAge age;
    @Schema(description = "메뉴", nullable = false, example = "붕어빵")
    @NotNull
    private String menu;
    @Schema(description = "썸네일", nullable = true, example = " https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
    private String thumbnail;

    public static PartyCreateDto toDto(Party party) {
        return PartyCreateDto.builder()
                .partyTitle(party.getPartyTitle())
                .partyContent(party.getPartyContent())
                .partyPlaceName(party.getPartyPlaceName())
                .partyTime(party.getPartyTime())
                .totalParticipant(party.getTotalParticipant())
                .longitude(party.getLongitude())
                .latitude(party.getLatitude())
                .gender(party.getGender())
                .category(party.getCategory())
                .menu(party.getMenu())
                .age(party.getAge())
                .thumbnail(party.getThumbnail())
                .build();
    }
}