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
    @Schema(description = "리뷰를 작성할 파티의 Id", example = "1")
    @NotNull
    private Long partyId; //파티 id
    @Schema(description = "리뷰 내용", example = "방장님이 너무 재밌었다.")
    @NotBlank
    private String content; //리뷰 내용
    @Schema(description = "파티 평점", example = "5")
    @NotNull
    @Max(5) @Min(0)
    private Integer rating; //온도
    @Schema(description = "리뷰 첨부사진 리스트", nullable = true, example = "['돈까스사진.jpg', '셀카.jpg']")
    private List<String> imgUrl; //리뷰 이미지
}
