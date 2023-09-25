package com.kr.matitting.repository;

import com.kr.matitting.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByOauthId(Long id);
}
