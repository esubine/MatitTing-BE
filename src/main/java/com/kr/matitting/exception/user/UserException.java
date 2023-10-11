package com.kr.matitting.exception.user;

import com.kr.matitting.exception.BaseException;
import com.kr.matitting.exception.BaseExceptionType;

public class UserException extends BaseException {
    private BaseExceptionType exceptionType;

    public UserException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
