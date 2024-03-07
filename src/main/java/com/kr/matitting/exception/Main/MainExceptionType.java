package com.kr.matitting.exception.Main;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MainExceptionType implements BaseExceptionType {

    INVALID_COORDINATE(1500, HttpStatus.BAD_REQUEST, "위도, 경도 값이 유효하지 않습니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}