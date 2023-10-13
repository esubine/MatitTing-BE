package com.kr.matitting.exception.menu;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MenuExceptionType implements BaseExceptionType {
    NOT_FOUND_MENU(900, HttpStatus.NOT_FOUND, "Menu를 찾을 수 없습니다.");
    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
