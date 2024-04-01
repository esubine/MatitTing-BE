package com.kr.matitting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReviewInfoRes {
    @Schema(description = "Review ID", example = "100")
    private Long reviewId;
    @Schema(description = "Sender 닉네임", example = "잔디개발자")
    private String nickname;
    @Schema(description = "리뷰 내용", example = "방장님 멋져요.")
    private String content;
    @Schema(description = "review rating", example = "50")
    private Integer rating;
    @Schema(description = "리뷰 첨부사진", example = "돈까스사진.jpg")
    private String reviewImg;
    @Schema(description = "리뷰 쓴 날짜", example = "2024-02-28")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createAt;
    @Schema(description = "리뷰를 쓴 사람인지에 대한 유무", example = "true")
    private Boolean isSelfReview;

    public static ReviewInfoRes toDto(Review review, User user) {
        Boolean selfReview = user == null ? false : review.getReviewer().getId().equals(user.getId());

        return new ReviewInfoRes(
                review.getId(),
                review.getReviewer().getNickname(),
                review.getContent(),
                review.getRating(),
                review.getImgUrl(),
                review.getCreateDate().toLocalDate(),
                selfReview
        );
    }
}
