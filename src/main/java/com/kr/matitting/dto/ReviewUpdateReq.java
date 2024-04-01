package com.kr.matitting.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateReq {
    @NotNull
    private Long reviewId;
    private String content;
    private Integer rating;
    private List<String> imgUrl;
}
