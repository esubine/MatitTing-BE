package com.kr.matitting.repository;

import com.kr.matitting.entity.Review;
import com.kr.matitting.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QReview.review;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Review> getHostReview(Pageable pageable, Long lastPartyReviewId, User user){
        return getHostReviewList(pageable, lastPartyReviewId, user);
    }

    private Slice<Review> getHostReviewList(Pageable pageable, Long lastHostReviewId, User user){
        List<Review> hostReviewList = queryFactory
                .select(review)
                .from(review)
                .where(review.receiver.eq(user),
                        ltHostReviewId(lastHostReviewId))
                .limit(pageable.getPageSize()+1)
                .orderBy(review.createDate.desc())
                .fetch();

        return checkLastPage(hostReviewList, pageable);
    }

    private BooleanExpression ltHostReviewId(Long lastHostReviewId) {
        return lastHostReviewId == 0L ? null : review.id.lt(lastHostReviewId);
    }

    private Slice<Review> checkLastPage(List<Review> hostReviewList, Pageable pageable) {
        boolean hasNext = false;
        if (hostReviewList.size() > pageable.getPageSize()) {
            hasNext = true;
            hostReviewList.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(hostReviewList, pageable, hasNext);
    }

}
