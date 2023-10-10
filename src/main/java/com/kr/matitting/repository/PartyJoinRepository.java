package com.kr.matitting.repository;

import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.entity.PartyJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyJoinRepository extends JpaRepository<PartyJoin, Long> {
    List<PartyJoin> findByPartyIdAndParentId(Long partyId, Long parentId);

    Optional<PartyJoin> findByPartyIdAndParentIdAndUserId(Long partyId, Long parentId, Long userId);
}
