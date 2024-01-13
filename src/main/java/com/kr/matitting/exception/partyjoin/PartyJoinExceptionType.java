package com.kr.matitting.exception.partyjoin;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PartyJoinExceptionType implements BaseExceptionType {
    NOT_FOUND_PARTY_JOIN(700, HttpStatus.NOT_FOUND, "파티 참가 정보가 없습니다."),
    NULL_POINT_PARTY_JOIN(701, HttpStatus.BAD_REQUEST, "파티 참가 데이터가 null로 유효하지 않습니다."),
    WRONG_STATUS(702, HttpStatus.BAD_REQUEST, "파티 참가 상태가 잘못되었습니다."),
    DUPLICATION_PARTY_JOIN(703, HttpStatus.FORBIDDEN, "파티 참가 상태가 이미 존재합니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
