package com.kr.matitting.exception.partyjoin;

import com.kr.matitting.exception.BaseException;
import com.kr.matitting.exception.BaseExceptionType;

public class PartyJoinException extends BaseException {
    private BaseExceptionType exceptionType;

    public PartyJoinException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
