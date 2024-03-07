package com.kr.matitting.exception.party;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PartyExceptionType implements BaseExceptionType {
    NOT_FOUND_PARTY(800, HttpStatus.NOT_FOUND, "파티 정보가 없습니다."),
    WRONG_STATUS_PARTY(801, HttpStatus.BAD_REQUEST, "파티 상태가 유효하지 않습니다."),
    NOT_FOUND_CONTENT(802, HttpStatus.BAD_REQUEST, "입력하지 않은 값들이 있습니다."),
    INVALID_UPDATE_VALUE(803, HttpStatus.BAD_REQUEST, "올바르지 못한 업데이트 값 입니다."),
    NOT_MINIMUM_PARTICIPANT(804, HttpStatus.BAD_REQUEST, "파티 인원은 2인 이상부터 가능합니다."),
    WRONG_TIME(805, HttpStatus.BAD_REQUEST, "파티 시간이 파티 마감 시간보다 빠르면 안됩니다.");
    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
