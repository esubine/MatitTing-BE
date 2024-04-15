package com.kr.matitting.repository;

import com.kr.matitting.constant.Role;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QPartyJoin.partyJoin;

@Repository
@RequiredArgsConstructor
public class PartyJoinRepositoryCustomImpl implements PartyJoinRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PartyJoin> getPartyJoin(Pageable pageable, User user, Role role) {
        List<PartyJoin> partyJoinList = getPartyJoinList(pageable, user, role);
        Long count = getPartyJoinCount(pageable, user, role);
        return new PageImpl<>(partyJoinList, pageable, count);
    }

    private Long getPartyJoinCount(Pageable pageable, User user, Role role) {
        JPAQuery<Long> jpaQuery = queryFactory
                .select(partyJoin.count())
                .from(partyJoin);

        if (role.equals(Role.HOST))
            jpaQuery.where(leaderIdEq(user.getId()));
        else
            jpaQuery.where(volunteerEq(user.getId()));

        return jpaQuery
                .limit(pageable.getPageSize())
                .offset(pageable.getPageNumber())
                .orderBy(partyJoin.createDate.desc())
                .fetchOne();
    }

    private List<PartyJoin> getPartyJoinList(Pageable pageable, User user, Role role) {
        JPAQuery<PartyJoin> jpaQuery = queryFactory
                .select(partyJoin)
                .from(partyJoin);

        if (role.equals(Role.HOST))
            jpaQuery.where(leaderIdEq(user.getId()));
        else
            jpaQuery.where(volunteerEq(user.getId()));

        return jpaQuery
                .limit(pageable.getPageSize())
                .offset(pageable.getPageNumber())
                .orderBy(partyJoin.createDate.desc())
                .fetch();
    }

    private BooleanExpression leaderIdEq(Long userId) {
        return partyJoin.leaderId.eq(userId);
    }

    private BooleanExpression volunteerEq(Long userId) {
        return partyJoin.userId.eq(userId);
    }
}