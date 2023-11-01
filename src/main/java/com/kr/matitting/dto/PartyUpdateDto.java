package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.PartyStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Optional;

public record PartyUpdateDto(
        @NotNull
        Long partyId, //파티아이디
        String partyTitle, //파티제목
        String partyContent, //파티 모집 글
        String menu, //메뉴
        Double longitude, //위도
        Double latitude, //경도
        PartyStatus status, //모집 여부
        Integer totalParticipant, //모집 인원
        Gender gender, //성별
        PartyAge age,
        String thumbnail, //썸네일
        LocalDateTime deadline, //마감시간
        LocalDateTime partyTime

) {
}
