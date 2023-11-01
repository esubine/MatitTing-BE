package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.entity.Party;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
@Getter
@Builder
public class ResponseSearchDto {
    @Schema(description = "파티 ID", example = "1")
    @NotNull
    private Long partyId;

    @Schema(description = "파티 제목", example = "붕어빵 먹을 사람")
    @NotNull
    private String title;

    @Schema(description = "파티 내용", example = "붕어빵은 팥이 근본입니다.")
    @NotNull
    private String content;

    @Schema(description = "파티 장소", example = "서울 서초구 126")
    @NotNull
    private String address;

    @Schema(description = "파티 시작 시간", example = "2023-10-24T10:00:00")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime partyTime;

    @Schema(description = "성별", example = "ALL")
    @NotNull
    private Gender gender;

    @Schema(description = "모집 인원", example = "4")
    @NotNull
    @Min(2)
    private Integer totalParticipant;

    @Schema(description = "현재 인원", example = "2")
    @NotNull
    @Min(1)
    private Integer participantCount;

    @Schema(description = "썸네일", nullable = true, example = " https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
    private String thumbnail;
    @Schema(description = "조회수", example = "100")
    private Integer hit;

    public static ResponseSearchDto toDto(Party party) {
        return ResponseSearchDto.builder()
                .partyId(party.getId())
                .title(party.getPartyTitle())
                .content(party.getPartyContent())
                .address(party.getAddress())
                .partyTime(party.getPartyTime())
                .gender(party.getGender())
                .totalParticipant(party.getTotalParticipant())
                .participantCount(party.getParticipantCount())
                .thumbnail(party.getThumbnail())
                .hit(party.getHit())
                .build();
    }
}