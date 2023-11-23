package com.kr.matitting.repository;

import com.kr.matitting.entity.Party;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QParty.party;

@Repository
@RequiredArgsConstructor
public class PartyRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public Slice<Party> getPartyList(double minLat, double maxLat, double minLon, double maxLon, Pageable pageable, Long lastPartyId) {

        List<Party> partyList = queryFactory
                .select(party)
                .from(party)
                .where(
                        party.latitude.goe(minLat),
                        party.latitude.loe(maxLat),
                        party.longitude.goe(minLon),
                        party.longitude.loe(maxLon),
                        ltPartyId(lastPartyId)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(party.id.desc())
                .fetch();

        return checkLastPage(partyList, pageable);
    }

    private BooleanExpression ltPartyId(Long lastPartyId) {

        if (lastPartyId == null) {
            return null;
        }

        return party.id.lt(lastPartyId);
    }

    private Slice<Party> checkLastPage(List<Party> results, Pageable pageable) {

        boolean hasNext = false;

        // 조회한 결과 개수가 요청한 페이지 사이즈보다 크면 뒤에 더 있음, hasNext = true
        if (results.size() > pageable.getPageSize()) {
            hasNext = true;
            results.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}