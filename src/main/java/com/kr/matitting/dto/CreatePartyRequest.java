package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyCategory;
import com.kr.matitting.constant.PartyGender;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
public class CreatePartyRequest {

    private String partyTitle;
    private String partyContent;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime partyTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime partyDeadline;

    private int totalParticipant;
    private String longitude;
    private String latitude;
    private PartyGender gender;
    private PartyCategory category;

}