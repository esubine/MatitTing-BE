package com.kr.matitting.repository;

import com.kr.matitting.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByParty_IdAndReceiver_IdAndReviewer_Id(Long partyId, Long receiverId, Long reviewerId);
}
