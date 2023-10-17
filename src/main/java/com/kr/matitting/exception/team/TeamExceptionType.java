package com.kr.matitting.exception.team;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamExceptionType implements BaseExceptionType {
    NOT_FOUND_TEAM(1000, HttpStatus.NOT_FOUND, "파티 팀 정보가 없습니다.");
    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
