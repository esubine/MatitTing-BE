package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import lombok.Data;

@Data
public class PartySearchCondDto {
    private String partyTitle;
    private String menu;
    private PartyStatus partyStatus;
    private int hit;
}
