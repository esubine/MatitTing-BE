package com.kr.matitting.dto;

import com.kr.matitting.constant.Orders;
import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Sorts;
import io.swagger.v3.oas.annotations.media.Schema;

public record PartySearchCondDto(
        @Schema(description = "검색어 키워드", nullable = true, example = "맛있팅 모임")
        String keyword,
        @Schema(description = "모집여부 필터", nullable = true, example = "RECRUIT")
        PartyStatus status,
        @Schema(description = "정렬", nullable = true, example = "{sortDto.sorts:HIT, sortDto.orders:ASC}")
        SortDto sortDto
) {

    public PartySearchCondDto(String keyword, PartyStatus status, SortDto sortDto) {
        this.keyword = keyword;
        this.status = status;
        if (sortDto == null) {
            sortDto = new SortDto(Sorts.HIT, Orders.DESC);
        }
        this.sortDto = sortDto;
    }

}
