package com.kr.matitting.dto;

import com.kr.matitting.constant.Orders;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Sorts;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

public record PartySearchCondDto(
        String title,
        String menu,
        PartyStatus status,
        SortDto sortDto,
        @NotNull
        Integer limit
) {

    public PartySearchCondDto(String title, String menu, PartyStatus status, SortDto sortDto, Integer limit) {
        this.title = title;
        this.menu = menu;
        this.status = status;
        if (sortDto == null) {
            sortDto = new SortDto(Sorts.HIT, Orders.DESC);
        }
        this.sortDto = sortDto;
        this.limit = limit;
    }

}
