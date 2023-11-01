package com.kr.matitting.dto;

import com.kr.matitting.constant.Sorts;
import com.kr.matitting.constant.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SortDto {
    private Sorts sorts;
    private Orders orders;
}