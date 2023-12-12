package com.kr.matitting.exception.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import com.kr.matitting.exception.BaseExceptionType;

@Getter
@AllArgsConstructor
public enum UserExceptionType implements BaseExceptionType {

    NOT_FOUND_USER(600, HttpStatus.NOT_FOUND, "회원 정보가 없습니다."),
    NULL_POINT_USER(601, HttpStatus.BAD_REQUEST, "회원 정보가 null로 유효하지 않습니다."),
    NOT_MATCH_USER(602, HttpStatus.BAD_REQUEST, "회원 정보가 일치하지 않습니다."),
    INVALID_ROLE_USER(603, HttpStatus.FORBIDDEN, "회원 ROLE이 유효하지 않습니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
