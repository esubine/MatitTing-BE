package com.kr.matitting.exception.chat;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ChatExceptionType implements BaseExceptionType {
    NOT_FOUND_CHAT_ROOM(1600, HttpStatus.BAD_REQUEST, "채팅방을 찾을 수 없습니다."),
    USER(1601, HttpStatus.BAD_REQUEST, "접속한 유저정보를 찾을 수 없습니다."),
    ALREADY_EXIST_ROOM(1602, HttpStatus.BAD_REQUEST, "이미 채팅방이 존재합니다."),
    NO_PRINCIPAL(1603, HttpStatus.BAD_REQUEST, "접근 권한이 없습니다."),
    NOT_FOUND_CHAT_USER_INFO(1604, HttpStatus.BAD_REQUEST, ""),

    IS_NOT_HAVE_CHAT_ROOM(1605, HttpStatus.OK, "참여중인 채팅방이 없습니다.")

    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
