package com.kr.matitting.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.BindException;

@Slf4j
@ControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity handleBaseEx(BaseException exception) {
        log.error("BaseException errorMessage(): {}", exception.getExceptionType().getErrorMessage());
        log.error("BaseException errorCode(): {}", exception.getExceptionType().getErrorCode());

        return new ResponseEntity(new ExceptionDto(exception.getExceptionType().getErrorCode(), exception.getExceptionType().getErrorMessage()), exception.getExceptionType().getHttpStatus());
    }

    //@Valid @ModelAttribute Exception
    @ExceptionHandler(BindException.class)
    public ResponseEntity handleValidEx(BindException exception) {
        log.error("@ValidException 발생!!", exception.getMessage());
        return new ResponseEntity(new ExceptionDto(2000, "필수 값이 입력되지 않았습니다."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity constraintViolationException(ConstraintViolationException e){
        log.error("NullPointerException 발생!!!", e.getMessage());
        return new ResponseEntity(new ExceptionDto(2001, "필수 값이 입력되지 않았습니다."), HttpStatus.BAD_REQUEST);
    }

    //@Valid @RequestBody Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodValidException(MethodArgumentNotValidException e){
        log.error("MethodArgumentNotValidException 발생!!!", e.getMessage());
        return new ResponseEntity(new ExceptionDto(2002, "요청된 값이 유효하지 않습니다."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity columnDuplication(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException 발생!!!", e.getMessage());
        return new ResponseEntity(new ExceptionDto(2003, "데이터 무결성 제약 조건 위반"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleEx(Exception exception) {
        exception.printStackTrace();
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @Data
    @AllArgsConstructor
    static class ExceptionDto {
        private Integer errorCode;
        private String errorMessage;
    }

}
