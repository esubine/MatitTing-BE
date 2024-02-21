package com.kr.matitting.service;

import com.kr.matitting.constant.NotificationType;
import com.kr.matitting.dto.ReviewDto;
import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.ReviewRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    public void setReview(User user, ReviewDto reviewDto) {
        User host = userRepository.findById(reviewDto.getUserId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));

        Review review = Review.builder()
                .host(host)
                .volunteer(user)
                .content(reviewDto.getContent())
                .build();
        Review saved = reviewRepository.save(review);

        notificationService.send(saved.getHost(), NotificationType.REVIEW, "후기가 도착했습니다.", saved.getContent());
    }
}
