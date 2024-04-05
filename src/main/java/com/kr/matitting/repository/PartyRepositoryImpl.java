package com.kr.matitting.repository;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.Sorts;
import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.PartyStatusReq;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.Team;
import com.kr.matitting.entity.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

import static com.kr.matitting.entity.QParty.party;
import static com.kr.matitting.entity.QTeam.team;

@Repository
@RequiredArgsConstructor
public class PartyRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public Page<Party> mainPage(MainPageDto mainPageDto, Pageable pageable) {
        List<Party> partyList = getPartyList(mainPageDto, pageable);
        Long count = getMainCount(mainPageDto);

        return new PageImpl<>(partyList, pageable, count);
    }

    public Page<Party> getMyParty(User user, PartyStatusReq partyStatusReq, Pageable pageable) {
        List<Party> myPartyList = getMyPartyList(user, partyStatusReq, pageable);
        Long myCount = getMyCount(partyStatusReq, user.getId());

        return new PageImpl<>(myPartyList, pageable, myCount);
    }

    private List<Party> getMyPartyList(User user, PartyStatusReq partyStatusReq, Pageable pageable) {
        JPAQuery<Team> teamJPAQuery = queryFactory
                .select(team)
                .from(team)
                .where(eqUserId(user.getId()));

        switch (partyStatusReq.getRole()) {
            case HOST:
            case VOLUNTEER:
                teamJPAQuery
                        .where(
                                eqRole(partyStatusReq.getRole())
                        );
        }
        List<Team> teamList = teamJPAQuery
                .where(eqStatus(partyStatusReq.getStatus()))
                .orderBy(team.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return teamList.stream().map(Team::getParty).toList();
    }

    private List<Party> getPartyList(MainPageDto mainPageDto, Pageable pageable) {
        JPAQuery<Party> partyJPAQuery = queryFactory
                .select(party)
                .from(party)
                .where(
                        eqPartyStatus(mainPageDto.getPartyStatus()),
                        getBuilder(mainPageDto.getLatitude(), mainPageDto.getLongitude()));

        if (mainPageDto.getSort() == Sorts.LATEST) {
            partyJPAQuery.orderBy(party.id.desc());
            partyJPAQuery.offset(pageable.getOffset());
            partyJPAQuery.limit(pageable.getPageSize());
        }

        List<Party> partyList = partyJPAQuery.fetch();

        if (mainPageDto.getSort() != Sorts.LATEST) {
            partyList.sort(Comparator.comparingDouble(party ->
                    calculateHaversine(mainPageDto.getLatitude(), mainPageDto.getLongitude(), party.getLatitude(), party.getLongitude())));

            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min((start + pageable.getPageSize()), partyList.size());
            partyList = partyList.subList(start, end);
        }
        return partyList;
    }

    private Long getMainCount(MainPageDto mainPageDto) {
        return queryFactory
                .select(party.count())
                .from(party)
                .where(eqPartyStatus(mainPageDto.getPartyStatus()), getBuilder(mainPageDto.getLatitude(), mainPageDto.getLongitude()))
                .fetchOne();
    }

    private Long getMyCount(PartyStatusReq partyStatusReq, Long userId) {
        return queryFactory
                .select(team.count())
                .from(team)
                .where(
                        eqUserId(userId),
                        List.of(Role.HOST, Role.VOLUNTEER).contains(partyStatusReq.getRole()) ? eqRole(partyStatusReq.getRole()) : null,
                        eqStatus(partyStatusReq.getStatus())
                )
                .fetchOne();
    }

    private BooleanBuilder getBuilder(double userLatitude, double userLongitude){
        double radius = 0.45;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(party.latitude.between(userLatitude - radius, userLatitude + radius));
        builder.and(party.longitude.between(userLongitude - radius, userLongitude + radius));

        return builder;
    }

    private BooleanExpression eqPartyStatus(PartyStatus partyStatus) {
        if (partyStatus != PartyStatus.RECRUIT) {
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

    private BooleanExpression eqUserId(Long userId) {
        return team.user.id.eq(userId);
    }
    private BooleanExpression eqRole(Role role) {
        return team.role.eq(role);
    }
    private BooleanExpression eqStatus(PartyStatus status) {
        return team.party.status.eq(status);
    }
    private BooleanExpression ltTeamId(Long teamId) {
        return teamId == 0L ? null : team.id.lt(teamId);
    }
    private Slice<Team> checkLastPage(List<Team> teamList, Pageable pageable) {
        boolean hasNext = false;
        if (teamList.size() > pageable.getPageSize()) {
            hasNext = true;
            teamList.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(teamList, pageable, hasNext);
    }
}