package com.kr.matitting.service;

import com.kr.matitting.constant.ReviewType;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.reivew.ReviewException;
import com.kr.matitting.exception.reivew.ReviewExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.ReviewRepository;
import com.kr.matitting.repository.ReviewRepositoryCustom;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final ReviewRepositoryCustom reviewRepositoryCustom;
    private final EntityFacade entityFacade;

    /**
     * 리뷰 리스트 조회
     */
    public ResponseReviewList getReviewList(Long userId, ReviewType reviewType, Pageable pageable) {
        User user = entityFacade.getUser(userId);

        List<ReviewGetRes> list;
        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = start + pageable.getPageSize() + 1;

        if (reviewType.equals(ReviewType.RECEIVER))
            list = user.getReceivedReviews().stream().map(review -> ReviewGetRes.toDto(review, review.getReviewer())).sorted(Comparator.comparing(ReviewGetRes::getReviewId).reversed()).toList();
        else
            list = user.getSendReviews().stream().map(review -> ReviewGetRes.toDto(review, review.getReceiver())).sorted(Comparator.comparing(ReviewGetRes::getReviewId).reversed()).toList();

        return new ResponseReviewList(list.subList(start, Math.min(list.size(), end)), new ResponsePageInfoDto(pageable.getPageNumber(), end < list.size()));
    }
    /**
     * 방장 리뷰 조회
     */
    public ReviewListRes getHostReviewList(Long userId, Pageable pageable) {
        User user = entityFacade.getUser(userId);

        Page<Review> hostReview = reviewRepositoryCustom.getHostReview(pageable, user);

        List<ReviewGetRes> reviewGetRes = hostReview.getContent().stream().map(review -> ReviewGetRes.toDto(review, review.getReviewer(), user)).toList();
        ResponsePageInfoDto responsePageInfoDto = new ResponsePageInfoDto(hostReview.getNumber(), hostReview.hasNext());

        return new ReviewListRes(reviewGetRes, responsePageInfoDto);
    }

    private Long getLastId(List<ReviewGetRes> reviewGetRes) {
        return reviewGetRes.isEmpty() ? null : reviewGetRes.get(reviewGetRes.size() - 1).getReviewId();
    }

    /**
     * 리뷰 상세 조회
     */
    public ReviewInfoRes getReview(Long userId, Long reviewId) {
        User user = entityFacade.getUser(userId);
        Review review = entityFacade.getReview(reviewId);
        return ReviewInfoRes.toDto(review, user);
    }

    /**
     * 리뷰 생성
     */
    public ReviewCreateRes createReview(ReviewCreateReq reviewCreateReq, Long userId) {
        User user = entityFacade.getUser(userId);
        Party party = entityFacade.getParty(reviewCreateReq.getPartyId());

        Optional<Review> byReview = reviewRepository.findByParty_IdAndReceiver_IdAndReviewer_Id(party.getId(), party.getUser().getId(), user.getId());
        if (byReview.isPresent())
            throw new ReviewException(ReviewExceptionType.DUPLICATION_REVIEW);


        if (party.getPartyTime().isAfter(LocalDateTime.now()))
            throw new ReviewException(ReviewExceptionType.NOT_START_PARTY);

        User receiver = entityFacade.getUser(party.getUser().getId());
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
        if (party.getReviews() == null)
            party.setReviews(new ArrayList<>());
        party.getReviews().add(review);
        if (receiver.getReceivedReviews() == null)
            receiver.setReceivedReviews(new ArrayList<>());
        receiver.getReceivedReviews().add(save);
        userRepository.save(receiver);
        if (user.getSendReviews() == null)
            user.setSendReviews(new ArrayList<>());
        user.getSendReviews().add(save);
        userRepository.save(user);

        return new ReviewCreateRes(save.getId());
    }

    /**
     * 리뷰 업데이트
     */
    public void updateReview(ReviewUpdateReq reviewUpdateReq, Long userId) {
        User user = entityFacade.getUser(userId);
        Review review = entityFacade.getReview(reviewUpdateReq.getReviewId());
        checkRole(user.getId(), review);

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
    public void deleteReview(ReviewDeleteReq reviewDeleteReq, Long userId) {
        User user = entityFacade.getUser(userId);
        Review review = entityFacade.getReview(reviewDeleteReq.getReviewId());
        checkRole(user.getId(), review);

        reviewRepository.deleteById(review.getId());
    }

    /**
     * 리뷰 주인 Check
     */
    private void checkRole(Long userId, Review review) {
        if (!userId.equals(review.getReviewer().getId()))
            throw new UserException(UserExceptionType.INVALID_ROLE_USER);
    }
}
