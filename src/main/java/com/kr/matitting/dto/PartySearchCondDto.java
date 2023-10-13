package com.kr.matitting.dto;

import com.kr.matitting.constant.PartyStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.Optional;

public record PartySearchCondDto(
        Optional<String> title,
        Optional<String> menu,
        Optional<PartyStatus> status,
        Optional<Map<String, String>> orders,
        @NotNull
        Integer limit
) {
    public void checkOrder() {
        if (orders == null) {
            orders.get().put("column", "hit");
            orders.get().put("type", "desc");
        }
    }

    public PartySearchCondDto partySearchCondDto(Optional<String> title, Optional<String> menu, Optional<PartyStatus> status, Optional<Map<String, String>> orders, Integer limit) {
        return new PartySearchCondDto(title, menu, status, orders, limit);
    }
}
