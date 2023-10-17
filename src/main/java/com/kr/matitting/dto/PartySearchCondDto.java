package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public record PartySearchCondDto(
        String title,
        String menu,
        PartyStatus status,
        Map<String, String> orders,
        @NotNull
        Integer limit
) {
    public Map<String, String> checkOrder() {
        if (orders == null) {
            Map<String, String> orders = new HashMap<String, String>();
            orders.put("column", "hit");
            orders.put("type", "desc");
            return orders;
        }
        return orders;
    }

    public PartySearchCondDto partySearchCondDto(String title, String menu, PartyStatus status, Map<String, String> orders, Integer limit) {
        return new PartySearchCondDto(title, menu, status, orders, limit);
    }
}
