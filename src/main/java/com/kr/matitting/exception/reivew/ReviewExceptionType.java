package com.kr.matitting.exception.reivew;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewExceptionType implements BaseExceptionType {

    NOT_FOUND_REVIEW(1700, HttpStatus.NOT_FOUND, "리뷰 정보가 없습니다."),
    NOT_START_PARTY(1701, HttpStatus.BAD_REQUEST, "파티가 시작하지 않았습니다."),
    DUPLICATION_REVIEW(1702, HttpStatus.CONFLICT, "작성한 리뷰가 존재합니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
