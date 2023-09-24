package com.kr.matitting.aop;

import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAop {
    @Pointcut("execution(* com.kr.matitting..*Controller*.*(..))")
    public void allController() {
    }

    @Pointcut("execution(* com.kr.matitting..*Service*.*(..))")
    public void allService() {
    }

    @Pointcut("execution(* com.kr.matitting..*Repository*.*(..))")
    public void allRepository() {
    }

//    @Around("allController() || allService() || allRepository()")
//    public Object logTrace() {
//        return new Object();
//    }
}
