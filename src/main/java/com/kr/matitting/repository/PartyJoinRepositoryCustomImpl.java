package com.kr.matitting.repository;

import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QParty.party;
import static com.kr.matitting.entity.QPartyJoin.partyJoin;

@Repository
@RequiredArgsConstructor
public class PartyJoinRepositoryCustomImpl implements PartyJoinRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<PartyJoin> getPartyJoin(Pageable pageable, Long lastPartyJoinId, User user, Role role) {
        return getPartyJoinList(pageable, lastPartyJoinId, user, role);
    }
    private Slice<PartyJoin> getPartyJoinList(Pageable pageable, Long lastPartyJoinId, User user, Role role) {
        JPAQuery<PartyJoin> jpaQuery = queryFactory
                .select(partyJoin)
                .from(partyJoin);

        if (role.equals(Role.HOST))
            jpaQuery.where(leaderIdEq(user.getId()));
        else
            jpaQuery.where(volunteerEq(user.getId()));
        jpaQuery.where(ltPartyJoinId(lastPartyJoinId));

        List<PartyJoin> partyJoinList = jpaQuery
                .limit(pageable.getPageSize())
                .orderBy(partyJoin.createDate.desc())
                .fetch();
        return checkLastPage(partyJoinList, pageable);
    }

    private BooleanExpression leaderIdEq(Long userId) {
        return partyJoin.leaderId.eq(userId);
    }

    private BooleanExpression volunteerEq(Long userId) {
        return partyJoin.userId.eq(userId);
    }

    private BooleanExpression ltPartyJoinId(Long lastPartyJoinId) {
        return lastPartyJoinId == 0L ? null : party.id.lt(lastPartyJoinId);
    }

    private Slice<PartyJoin> checkLastPage(List<PartyJoin> partyJoinList, Pageable pageable) {
        boolean hasNext = false;
        if (partyJoinList.size() > pageable.getPageSize()) {
            hasNext = true;
            partyJoinList.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(partyJoinList, pageable, hasNext);
    }

}