package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import lombok.Data;

@Data
public class PartySearchCondDto {
    private String title;
    private String menu;
    private PartyStatus status;
    private int hit;
}
