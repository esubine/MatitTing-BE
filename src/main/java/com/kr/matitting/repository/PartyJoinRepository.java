package com.kr.matitting.repository;

import com.kr.matitting.entity.PartyJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
    Optional<List<PartyJoin>> findByPartyIdAndLeaderId(Long partyId, Long parentId);

    Optional<PartyJoin> findByPartyIdAndLeaderIdAndUserId(Long partyId, Long parentId, Long userId);
}
