package com.kr.matitting.repository;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.entity.Party;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.kr.matitting.entity.QParty.party;


@Repository
@RequiredArgsConstructor
public class PartyRepositoryCustomImpl implements PartyRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Party> searchPage(Pageable pageable, PartySearchCondDto partySearchCondDto) {
        List<Party> partyList = getPartyList(pageable, partySearchCondDto);
        Long count = getCount(partySearchCondDto);

        return new PageImpl<>(partyList, pageable, count);
    }

    private List<Party> getPartyList(Pageable pageable, PartySearchCondDto partySearchCondDto) {
        return  queryFactory
                .select(party)
                .from(party)
                .where(ticketSearchPredicate(partySearchCondDto.keyword()), stateEq(partySearchCondDto.status()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(partySort(pageable))
                .fetch();
    }

    private Long getCount(PartySearchCondDto partySearchCondDto) {
        return queryFactory
                .select(party.count())
                .from(party)
                .where(ticketSearchPredicate(partySearchCondDto.keyword()), stateEq(partySearchCondDto.status()))
                .fetchOne();
    }

    private BooleanBuilder ticketSearchPredicate(String keyword) {
        return new BooleanBuilder(
                titleLike(keyword)
                        .or(menuLike(keyword))
                        .or(contentLike(keyword))
                        .or(addressLike(keyword))
        );
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? party.partyTitle.contains(title) : null;
    }

    private BooleanExpression menuLike(String menu) {
        return StringUtils.hasText(menu) ? party.menu.contains(menu) : null;
    }

    private BooleanExpression contentLike(String content) {
        return StringUtils.hasText(content) ? party.partyContent.contains(content) : null;
    }

    private BooleanExpression addressLike(String address) {
        return StringUtils.hasText(address) ? party.address.contains(address) : null;
    }

    private BooleanExpression stateEq(PartyStatus partyStatus) {
        if (ObjectUtils.isEmpty(partyStatus)) {
            return null;
        }
        return party.status.eq(partyStatus);
    }

    private BooleanExpression ltPartyId(Long lastPartyId) {
        return lastPartyId == 0L ? null : party.id.lt(lastPartyId);
    }

    private Slice<Party> checkLastPage(List<Party> partyList, Pageable pageable) {
        boolean hasNext = false;
        if (partyList.size() > pageable.getPageSize()) {
            hasNext = true;
            partyList.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(partyList, pageable, hasNext);
    }

    private OrderSpecifier<?> partySort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "HIT": //조회순
                        return new OrderSpecifier(direction, party.hit);
                    case "LATEST": //최신순(생성순)
                        return new OrderSpecifier(direction, party.createDate);
                    case "DEADLINE": //마감순
                        return new OrderSpecifier(direction, party.deadline);
                }
            }
        }
        return new OrderSpecifier(Order.ASC, NullExpression.DEFAULT, OrderSpecifier.NullHandling.Default);
    }
}
