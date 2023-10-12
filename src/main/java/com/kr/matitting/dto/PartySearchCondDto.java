package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class PartySearchCondDto {
    private String title;
    private String menu;
    private PartyStatus status;
    private Map<String, String> orders;
    @NotNull
    private int limit;

    public void checkOrder() {
        if (this.orders == null) {
            this.orders = new HashMap<String, String>();
            orders.put("column", "hit");
            orders.put("type", "desc");
        }
    }
}
