package com.kr.matitting.exception.token;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TokenExceptionType implements BaseExceptionType {
    NOT_FOUND_ACCESS_TOKEN(1100, HttpStatus.BAD_REQUEST, "Access Token이 없습니다."),
    INVALID_ACCESS_TOKEN(1101, HttpStatus.FORBIDDEN, "Access Token이 유효하지 않습니다."),
    UNAUTHORIZED_ACCESS_TOKEN(1102, HttpStatus.UNAUTHORIZED, "Access Token이 Expire 됐습니다."),
    BLACK_LIST_ACCESS_TOKEN(1103, HttpStatus.FORBIDDEN, "Black List Access Token 입니다."),
    NOT_FOUND_SOCIAL_TOKEN(1104, HttpStatus.BAD_REQUEST, "소셜 Token을 찾을 수 없습니다."),
    NOT_FOUND_REFRESH_TOKEN(1200, HttpStatus.BAD_REQUEST, "Refresh Token이 없습니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
