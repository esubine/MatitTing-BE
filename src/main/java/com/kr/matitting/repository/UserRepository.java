package com.kr.matitting.repository;

import com.kr.matitting.entity.SocialType;
import com.kr.matitting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    Optional<User> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
