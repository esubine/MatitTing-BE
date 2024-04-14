package com.kr.matitting.repository;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> getHostReview(Pageable pageable, User user) {
        List<Review> hostReviewList = getHostReviewList(pageable, user);
        Long count = getReviewCount(user);

        return new PageImpl<>(hostReviewList, pageable, count);
    }

    private Long getReviewCount(User user) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(review.receiver.eq(user))
                .fetchOne();
    }

    private List<Review> getHostReviewList(Pageable pageable, User user){
        return  queryFactory
                .select(review)
                .from(review)
                .where(review.receiver.eq(user))
                .limit(pageable.getPageSize())
                .offset(pageable.getPageNumber())
                .orderBy(review.createDate.desc())
                .fetch();
    }

}
