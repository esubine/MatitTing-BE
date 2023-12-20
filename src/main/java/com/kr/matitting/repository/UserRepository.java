package com.kr.matitting.repository;

import com.kr.matitting.constant.SocialType;
import com.kr.matitting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);
    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
    Optional<User> findByNickname(String nickname);
}
