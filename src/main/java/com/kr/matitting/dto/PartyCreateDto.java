package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.entity.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "파티 생성 DTO")
public class PartyCreateDto {
    @Schema(description = "사용자 ID", nullable = false, example = "1")
    @NotNull
    private Long user_id;
    @Schema(description = "파티 제목", nullable = false, example = "붕어빵 먹을 사람")
    @NotNull
    private String title;
    @Schema(description = "파티 내용", nullable = false, example = "붕어빵은 팥이 근본입니다.")
    @NotNull
    private String content;
    @Schema(description = "파티 시작 시간", nullable = false, example = "2023-10-24T10:00:00")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime partyTime;
    @Schema(description = "파티 모집 마감 시간", nullable = true, example = "2023-10-24T09:00:00")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;
    @Schema(description = "모집 인원", nullable = false, example = "4")
    @NotNull
    @Min(2)
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
    @Schema(description = "메뉴", nullable = true, example = "붕어빵")
    private String menu;
    @Schema(description = "썸네일", nullable = true, example = " https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
    private String thumbnail;

    public static PartyCreateDto toDto(Party party) {
        return PartyCreateDto.builder()
                .user_id(party.getId())
                .title(party.getPartyTitle())
                .content(party.getPartyContent())
                .partyTime(party.getPartyTime())
                .deadline(party.getDeadline())
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