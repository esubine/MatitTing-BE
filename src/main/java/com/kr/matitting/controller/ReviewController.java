package com.kr.matitting.controller;

import com.kr.matitting.dto.ReviewDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> setReview(@AuthenticationPrincipal User user, @RequestBody ReviewDto reviewDto) {
        reviewService.setReview(user, reviewDto);
        return ResponseEntity.ok(null);
    }


}
