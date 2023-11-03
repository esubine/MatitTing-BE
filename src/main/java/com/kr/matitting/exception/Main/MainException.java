package com.kr.matitting.exception.main;

import com.kr.matitting.exception.BaseException;
import com.kr.matitting.exception.BaseExceptionType;

public class MainException extends BaseException {

    private BaseExceptionType exceptionType;

    public MainException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}