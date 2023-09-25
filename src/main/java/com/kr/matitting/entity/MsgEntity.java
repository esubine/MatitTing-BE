package com.kr.matitting.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MsgEntity {
    private String status;
    private Object result;

    public MsgEntity(String status, Object result) {
        this.status = status;
        this.result  = result;
    }
}
