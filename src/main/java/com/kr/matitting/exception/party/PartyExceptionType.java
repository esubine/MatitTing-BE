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
    NOT_FOUND_CONTENT(400, HttpStatus.NOT_FOUND, "입력하지 않은 값들이 있습니다.");
    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
