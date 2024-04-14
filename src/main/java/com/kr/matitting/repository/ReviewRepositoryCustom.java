package com.kr.matitting.repository;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> getHostReview(Pageable pageable, User user);
}