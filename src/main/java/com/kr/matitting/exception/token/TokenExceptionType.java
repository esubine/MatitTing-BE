package com.kr.matitting.exception.token;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenExceptionType implements BaseExceptionType {
    INVALID_ACCESS_TOKEN(1000, HttpStatus.BAD_REQUEST, "Access Token이 유효하지 않습니다."),
    INVALID_REFRESH_TOKEN(1001, HttpStatus.BAD_REQUEST, "Refresh Token이 유효하지 않습니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
