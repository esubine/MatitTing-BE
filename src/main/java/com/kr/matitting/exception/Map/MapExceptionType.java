package com.kr.matitting.exception.Map;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MapExceptionType implements BaseExceptionType {

    FAILED_AUTHORIZATION(1300, HttpStatus.UNAUTHORIZED, "카카오 맵 Authorization이 실패했습니다."),
    FAILED_GET_DATA(1301, HttpStatus.BAD_REQUEST, "카카오 맵에서 데이터를 받아오지 못했습니다."),
    KAKAO_MAP_SERVER_ERROR(1302, HttpStatus.INTERNAL_SERVER_ERROR, "카카오 맵 서버 오류입니다.");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
