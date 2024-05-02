package com.kr.matitting.controller;

import com.kr.matitting.constant.ReviewType;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.reivew.ReviewException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

    @Operation(summary = "리뷰 리스트 조회", description = "나에게 온 리뷰 리스트를 불러오는 API \n\n" +
            "Request \n\n" +
            "\t ReviewType : 내가 받은 리뷰 리스트 or 내가 보낸 리뷰 리스트를 조회 \n\n" +
            "\t page : pageNumber \n\n" +
            "\t size : limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공", content = @Content(schema = @Schema(implementation = ResponseReviewList.class))),
    })
    @GetMapping
    public ResponseEntity<ResponseReviewList> getReviewList(@AuthenticationPrincipal User user,
                                                            @RequestParam ReviewType reviewType,
                                                            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewList(user, reviewType, pageable));
    }

    @Operation(summary = "방장 리뷰 리스트 조회", description = "방장의 리뷰 리스트를 불러오는 API \n\n" +
            "Request \n\n" +
            "\t page : pageNumber \n\n" +
            "\t size : limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "방장 리뷰 리스트 조회 성공", content = @Content(schema = @Schema(implementation = ReviewListRes.class))),
            @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class)))
    })
    @GetMapping("/host")
    public ResponseEntity<ReviewListRes> getHostReviewList(@RequestParam Long hostId,
                                                           @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reviewService.getHostReviewList(hostId, pageable));
    }

    @Operation(summary = "리뷰 상세 조회", description = "리뷰 상세 정보를 불러오는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 상세 조회 성공", content = @Content(schema = @Schema(implementation = ReviewInfoRes.class))),
            @ApiResponse(responseCode = "404(1700)", description = "리뷰 정보가 없음", content = @Content(schema = @Schema(implementation = ReviewException.class)))
    })
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewInfoRes> getReview(@AuthenticationPrincipal User user, @PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(user, reviewId));
    }

    @Operation(summary = "리뷰 작성", description = "방장에게 리뷰를 작성하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 작성 성공", content = @Content(schema = @Schema(implementation = ReviewCreateRes.class))),
            @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
            @ApiResponse(responseCode = "404(800)", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyException.class))),
            @ApiResponse(responseCode = "400(1701)", description = "파티가 시작하지 않음", content = @Content(schema = @Schema(implementation = ReviewException.class))),
            @ApiResponse(responseCode = "409(1702)", description = "작성한 리뷰가 이미 존재", content = @Content(schema = @Schema(implementation = ReviewException.class)))
    })
    @PostMapping
    public ResponseEntity<ReviewCreateRes> createReview(@RequestBody ReviewCreateReq reviewCreateReq, @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.createReview(reviewCreateReq, user));
    }

    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "403(602)", description = "요청한 회원정보가 잘못되었습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
            @ApiResponse(responseCode = "404(1700)", description = "리뷰 정보가 없음", content = @Content(schema = @Schema(implementation = ReviewException.class)))
    })
    @PatchMapping
    public ResponseEntity<?> updateReview(@RequestBody ReviewUpdateReq reviewUpdateReq, @AuthenticationPrincipal User user) {
        reviewService.updateReview(reviewUpdateReq, user);
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "403(602)", description = "요청한 회원정보가 잘못되었습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
            @ApiResponse(responseCode = "404(1700)", description = "리뷰 정보가 없음", content = @Content(schema = @Schema(implementation = ReviewException.class)))
    })
    @DeleteMapping
    public ResponseEntity<?> deleteReview(@RequestBody ReviewDeleteReq reviewDeleteReq, @AuthenticationPrincipal User user) {
        reviewService.deleteReview(reviewDeleteReq, user);
        return ResponseEntity.ok(null);
    }
}
