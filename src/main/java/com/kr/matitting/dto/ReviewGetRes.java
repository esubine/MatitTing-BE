package com.kr.matitting.dto;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Math.*;

@Getter
@AllArgsConstructor
public class ReviewGetRes {
    @Schema(description = "Review ID", example = "100")
    private Long reviewId;
    @Schema(description = "User Profile Image", example = "증명사진.jpg")
    private String userProfileImg;
    //TODO: 보낸 사람과 방장 nickname을 구분할 필요가 있어 보임!!
    @Schema(description = "방장 닉네임", example = "방장")
    private String hostNickname;
    @Schema(description = "Sender 닉네임", example = "새싹개발자")
    private String senderNickname;
    @Schema(description = "review rating", example = "5")
    private Integer rating;
    @Schema(description = "리뷰 내용", example = "방장님 멋져요.")
    private String content;
    @Schema(description = "리뷰 첨부사진 리스트", example = "['돈까스사진.jpg', '셀카.jpg']")
    private List<String> reviewImg;
    @Schema(description = "리뷰 생성일자", example = "2024-03-28T14:45:30.123456789")
    private LocalDateTime createAt;

    public static ReviewGetRes toDto(Review review, User user) {
        return new ReviewGetRes(
                review.getId(),
                user.getImgUrl(),
                null,
                user.getNickname(),
                review.getRating(),
                review.getContent(),
                review.getImgUrl(),
                review.getCreateDate());
    }

    public static ReviewGetRes toDto(Review review, User user, User host) {
        return new ReviewGetRes(
                review.getId(),
                user.getImgUrl(),
                host.getNickname(),
                user.getNickname(),
                review.getRating(),
                review.getContent(),
                review.getImgUrl(),
                review.getCreateDate());
    }
}
