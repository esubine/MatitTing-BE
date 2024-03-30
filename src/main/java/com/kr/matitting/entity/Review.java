package com.kr.matitting.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "REVIEW")
@Entity
public class Review extends BaseTimeEntity {
    @Schema(description = "리뷰 id", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id; //리뷰 id

    @Schema(description = "리뷰 내용", example = "화끈합니다.")
    private String content; //리뷰 내용

    @Schema(description = "만족도", example = "57")
    private Integer rating; //온도

    @Schema(description = "리뷰 사진", example = "파스타.jpg")
    private List<String> imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;
}
