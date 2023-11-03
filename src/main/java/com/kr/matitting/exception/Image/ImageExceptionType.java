package com.kr.matitting.exception.Image;

import com.kr.matitting.exception.BaseExceptionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ImageExceptionType implements BaseExceptionType {

    FAILED_CONVERT_FILE(1400, HttpStatus.BAD_REQUEST, "MultipartFile -> File 전환 실패");

    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;
}
