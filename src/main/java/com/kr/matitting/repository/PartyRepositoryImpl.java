package com.kr.matitting.repository;

import com.kr.matitting.entity.Party;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QParty.party;

@Repository
@RequiredArgsConstructor
public class PartyRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public List<Party> getPartyList(double minLat, double maxLat, double minLon, double maxLon, Pageable pageable) {

        List<Party> partyList = queryFactory
                .select(party)
                .from(party)
                .where(
                        party.latitude.goe(minLat),
                        party.latitude.loe(maxLat),
                        party.longitude.goe(minLon),
                        party.longitude.loe(maxLon)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(party.id.desc())
                .fetch();

        return partyList;
    }
}