package com.kr.matitting.dto;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.PartyStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Optional;

public record PartyUpdateDto(
        @NotNull
        Long partyId, //파티아이디
        Optional<String> partyTitle, //파티제목
        Optional<String> partyContent, //파티 모집 글
        Optional<String> menu, //메뉴
        Optional<String> longitude, //위도
        Optional<String> latitude, //경도
        Optional<PartyStatus> status, //모집 여부
        Optional<Integer> totalParticipant, //모집 인원
        Optional<Gender> gender, //성별
        Optional<String> thumbnail, //썸네일
        Optional<LocalDateTime> deadline //마감시간
) {
}
