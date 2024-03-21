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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/review")
public class ReviewController {
    private final ReviewService reviewService;

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
}
