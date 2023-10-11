package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import lombok.Data;

import java.util.Map;

@Data
public class PartySearchCondDto {
    private String title;
    private String menu;
    private PartyStatus status;
    private Map<String, String> orders;
    private int limit;
}
