package com.kr.matitting.exception.reivew;

import com.kr.matitting.exception.BaseException;
import com.kr.matitting.exception.BaseExceptionType;

public class ReviewException extends BaseException {
    private BaseExceptionType exceptionType;

    public ReviewException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
