package com.kr.matitting.dto;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

import static java.lang.Math.*;

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
    @Schema(description = "리뷰 생성일자", example = "2024-03-28T14:45:30.123456789")
    private LocalDateTime createAt;

    public static ReviewGetRes toDto(Review review, User user) {
        return new ReviewGetRes(
                review.getId(),
                user.getImgUrl(),
                user.getNickname(),
                review.getRating(),
                review.getContent(),
                review.getImgUrl(),
                review.getCreateDate());
    }
}
