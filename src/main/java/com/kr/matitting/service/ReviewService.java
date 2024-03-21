package com.kr.matitting.service;

import com.kr.matitting.constant.ReviewType;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.reivew.ReviewException;
import com.kr.matitting.exception.reivew.ReviewExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.ReviewRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;

    /**
     * 리뷰 생성
     */
    public ReviewCreateRes createReview(ReviewCreateReq reviewCreateReq, User user) {
        Optional<Review> byReview = reviewRepository.findByParty_IdAndReceiver_IdAndReviewer_Id(reviewCreateReq.getPartyId(), reviewCreateReq.getUserId(), user.getId());
        if (byReview.isPresent())
            throw new ReviewException(ReviewExceptionType.DUPLICATION_REVIEW);

        Party party = partyRepository.findById(reviewCreateReq.getPartyId()).orElseThrow(() -> new PartyException(PartyExceptionType.NOT_FOUND_PARTY));

        if (party.getPartyTime().isAfter(LocalDateTime.now()))
            throw new ReviewException(ReviewExceptionType.NOT_START_PARTY);

        User receiver = userRepository.findById(reviewCreateReq.getUserId()).orElseThrow(() -> new UserException(UserExceptionType.NOT_FOUND_USER));
        Review review = Review.builder()
                .content(reviewCreateReq.getContent())
                .rating(reviewCreateReq.getRating())
                .imgUrl(reviewCreateReq.getImgUrl())
                .party(party)
                .reviewer(user)
                .receiver(receiver)
                .build();
        Review save = reviewRepository.save(review);

        // 리뷰를 받는 사용자의 receivedReviews 리스트에 추가
        receiver.getReceivedReviews().add(save);
        userRepository.save(receiver);
        user.getSendReviews().add(save);
        userRepository.save(user);

        return new ReviewCreateRes(save.getId());
    }

    /**
     * 리뷰 업데이트
     */
    public void updateReview(ReviewUpdateReq reviewUpdateReq, User user) {
        Review review = getReview(reviewUpdateReq.getReviewId());
        checkRole(user, review);

        if (reviewUpdateReq.getContent() != null)
            review.setContent(reviewUpdateReq.getContent());
        if (reviewUpdateReq.getRating() != null)
            review.setRating(reviewUpdateReq.getRating());
        if (reviewUpdateReq.getImgUrl() != null)
            review.setImgUrl(reviewUpdateReq.getImgUrl());
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(ReviewDeleteReq reviewDeleteReq, User user) {
        Review review = getReview(reviewDeleteReq.getReviewId());
        checkRole(user, review);

        reviewRepository.deleteById(review.getId());
    }

    /**
     * 리뷰 주인 Check
     */
    private void checkRole(User user, Review review) {
        if (!user.getId().equals(review.getReviewer().getId()))
            throw new UserException(UserExceptionType.INVALID_ROLE_USER);
    }

    /**
     * 리뷰 DB 조회
     */
    private Review getReview(Long reviewId) {
        return reviewRepository.findById(reviewId).orElseThrow(() -> new ReviewException(ReviewExceptionType.NOT_FOUND_REVIEW));
    }
}
