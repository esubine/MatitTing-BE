package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 생성 DTO")
public class ReviewCreateReq {
    @NotNull
    private Long partyId; //파티 id
    @NotBlank
    private String content; //리뷰 내용
    @NotNull
    @Max(5) @Min(0)
    private Integer rating; //온도
    private List<String> imgUrl; //리뷰 이미지
}
