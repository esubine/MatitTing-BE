package com.kr.matitting.repository;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewRepositoryCustom {
    Slice<Review> getHostReview(Pageable pageable, Long lastPartyReviewId, User user);
}