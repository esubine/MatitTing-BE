package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "리뷰 생성 DTO")
public class ReviewCreateReq {
    @NotNull
    private Long userId; //방장 id
    private Long partyId; //파티 id
    @NotBlank
    private String content; //리뷰 내용
    @NotNull
    private Integer rating; //온도
    private String imgUrl; //리뷰 이미지
}
