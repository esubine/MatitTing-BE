package com.kr.matitting.dto;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewGetRes {
    @Schema(description = "Review ID", example = "100")
    private Long reviewId;
    @Schema(description = "User Profile Image", example = "증명사진.jpg")
    private String userProfileImg;
    @Schema(description = "Sender or Receiver 닉네임", example = "새싹개발자")
    private String nickname;
    @Schema(description = "review rating", example = "50")
    private Integer rating;
    @Schema(description = "리뷰 내용", example = "방장님 멋져요.")
    private String content;
    @Schema(description = "리뷰 첨부사진", example = "돈까스사진.jpg")
    private String reviewImg;

    public static ReviewGetRes toDto(Review review, User user) {
        return new ReviewGetRes(
                review.getId(),
                user.getImgUrl(),
                user.getNickname(),
                review.getRating(),
                review.getContent().substring(0, Math.max(review.getContent().length(),9)) + " ...",
                review.getImgUrl());
    }
}
