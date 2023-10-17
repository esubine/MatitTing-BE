package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyCategory;
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
    private String longitude;
    @NotNull
    private String latitude;
    @NotNull
    private Gender gender;
    @NotNull
    private PartyCategory category;
    @NotNull
    private PartyAge age;
    @NotNull
    private String menu;
    private String thumbnail;
}