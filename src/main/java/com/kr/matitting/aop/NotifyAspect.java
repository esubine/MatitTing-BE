package com.kr.matitting.aop;

import com.kr.matitting.entity.Notification;
import com.kr.matitting.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Slf4j
@Aspect
@EnableWebMvc
@Component
@EnableAsync
@RequiredArgsConstructor
public class NotifyAspect {
    private final NotificationService notificationService;

    @Pointcut("@annotation(com.kr.matitting.annotation.Notify)")
    public void annotationPointcut() {
    }

    @Async
    @AfterReturning(pointcut = "annotationPointcut()", returning = "result")
    public void checkValue(JoinPoint joinPoint, ResponseEntity<?> responseEntity) throws Throwable {
        Object body = responseEntity.getBody();

    }
}
