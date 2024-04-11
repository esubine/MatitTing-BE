package com.kr.matitting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseReviewList {
    private List<ReviewGetRes> reviewGetResList;
    private ResponsePageInfoDto pageInfo;
}
