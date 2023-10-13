package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyGender;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreatePartyRequest {

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
    private PartyGender gender;
    @NotNull
    private PartyCategory category;
    @NotNull
    private PartyAge age;
    @NotNull
    private String menu;
    private String thumbnail;

}