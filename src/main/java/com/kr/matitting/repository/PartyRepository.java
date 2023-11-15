package com.kr.matitting.repository;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByPartyTitle(String partyTitle);

    List<Party> findByUserIdAndStatus(Long userId, PartyStatus status);

    @Query("select p from Party p " +
            "join fetch p.user " +
            "where p.id = :partyId")
    Optional<Party> findByIdFJUser(@Param("partyId") Long partyId);
}
