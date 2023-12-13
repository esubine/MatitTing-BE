package com.kr.matitting.repository;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Sorts;
import com.kr.matitting.entity.Party;
import com.querydsl.core.BooleanBuilder;
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

    public Slice<Party> getPartyList(double userLatitude, double userLongitude, PartyStatus partyStatus, Sorts sorts, Long lastPartyId, Pageable pageable) {
        List<Party> parties;

        if (sorts == Sorts.LATEST) {
            parties = queryFactory
                    .select(party)
                    .from(party)
                    .where(
                            eqPartyStatus(partyStatus),
                            getBuilder(userLatitude, userLongitude),
                            ltPartyId(lastPartyId))
                    .orderBy(party.id.desc())
                    .fetch();
        } else {
            parties = queryFactory
                    .select(party)
                    .from(party)
                    .where(
                            eqPartyStatus(partyStatus),
                            getBuilder(userLatitude, userLongitude),
                            ltPartyId(lastPartyId))
                    .fetch();

            parties.sort((party1, party2) -> {
                double distance1 = calculateHaversine(userLatitude, userLongitude, party1.getLatitude(), party1.getLongitude());
                double distance2 = calculateHaversine(userLatitude, userLongitude, party2.getLatitude(), party2.getLongitude());
                return Double.compare(distance1, distance2);
            });
        }
        return checkLastPage(parties, pageable);
    }

    private BooleanExpression ltPartyId(Long lastPartyId) {
        return lastPartyId == 0L ? null : party.id.lt(lastPartyId);
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

    private BooleanBuilder getBuilder(double userLatitude, double userLongitude){
        double radius = 0.045;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(party.latitude.between(userLatitude - radius, userLatitude + radius));
        builder.and(party.longitude.between(userLongitude - radius, userLongitude + radius));

        return builder;
    }

    private BooleanExpression eqPartyStatus(PartyStatus partyStatus) {
        if (partyStatus == null) {
            partyStatus = PartyStatus.RECRUIT;
        } else if (partyStatus == PartyStatus.FINISH) {
            return null;
        }

        return party.status.eq(partyStatus);
    }

    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        // 지구의 반지름 (단위: km)
        double R = 6371;

        // 두 지점의 위도 및 경도 차이
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Haversine 공식 계산
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리 반환
        return R * c;
    }
}