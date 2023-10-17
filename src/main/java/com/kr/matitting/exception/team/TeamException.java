package com.kr.matitting.exception.team;

import com.kr.matitting.entity.Team;
import com.kr.matitting.exception.BaseException;
import com.kr.matitting.exception.BaseExceptionType;

public class TeamException extends BaseException {
    private BaseExceptionType exceptionType;

    public TeamException(BaseExceptionType exceptionType) {
        this.exceptionType = exceptionType;
    }
    @Override
    public BaseExceptionType getExceptionType() {
        return exceptionType;
    }
}
