package com.kr.matitting.exception.party;

import com.kr.matitting.exception.BaseException;
import com.kr.matitting.exception.BaseExceptionType;

public class PartyException extends BaseException {

    private BaseExceptionType exceptionType;

    public PartyException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
