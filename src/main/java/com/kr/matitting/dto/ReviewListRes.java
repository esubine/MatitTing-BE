package com.kr.matitting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ReviewListRes {
    private List<ReviewGetRes> reviewGetResList;

    private ResponseNoOffsetDto noOffsetDto;
}
