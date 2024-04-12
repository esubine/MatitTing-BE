package com.kr.matitting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateReq {
    @Schema(description = "수정할 리뷰의 Id", example = "1")
    @NotNull
    private Long reviewId;
    @Schema(description = "리뷰 내용", example = "수정본")
    private String content;
    @Schema(description = "파티 평점", example = "3")
    private Integer rating;
    @Schema(description = "리뷰 첨부사진 리스트", nullable = true, example = "['돈까스사진.jpg', '셀카.jpg']")
    private List<String> imgUrl;
}
