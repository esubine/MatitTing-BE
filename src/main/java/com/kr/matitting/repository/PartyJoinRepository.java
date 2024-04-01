package com.kr.matitting.repository;

import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.PartyJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
    Optional<PartyJoin> findByPartyIdAndUserId(Long partyId, Long userId);
    Optional<PartyJoin> findByPartyIdAndLeaderIdAndUserId(Long partyId, Long leaderId, Long userId);
    List<PartyJoin> findAllByLeaderId(Long userId);
    List<PartyJoin> findAllByUserId(Long userId);
}
