package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.entity.Party;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Builder
public class PartyCreateDto {

    @NotNull
    private Long user_id;
    @NotNull
    private String title;
    @NotNull
    private String content;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime partyTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;
    @NotNull
    @Min(2)
    private int totalParticipant;
    @NotNull
    private double longitude;
    @NotNull
    private double latitude;
    @NotNull
    private Gender gender;
    @NotNull
    private PartyCategory category;
    @NotNull
    private PartyAge age;
    @NotNull
    private String menu;
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