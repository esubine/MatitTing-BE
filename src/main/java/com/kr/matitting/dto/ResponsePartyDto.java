package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.entity.Party;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ResponsePartyDto(
        @NotNull
        String partyTitle,
        @NotNull
        String partyContent,
        @NotNull
        String address,
        @NotNull
        double longitude,
        @NotNull
        double latitude,
        @NotNull
        PartyStatus status,
        @NotNull
        Gender gender,
        @NotNull
        PartyAge age,
        @NotNull
        LocalDateTime deadline,
        @NotNull
        LocalDateTime partyTime,
        @NotNull
        Integer totalParticipate,
        @NotNull
        Integer participate,
        @NotNull
        String thumbnail
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
                party.getThumbnail());
    }
}
