package com.kr.matitting.repository;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.entity.Party;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Page<Party> searchPage(PartySearchCondDto partySearchCondDto, Pageable pageable) {
        List<Party> content = getPartyList(partySearchCondDto, pageable);
        Long count = getCount(partySearchCondDto);

        return new PageImpl<>(content, pageable, count);
    }

    private List<Party> getPartyList(PartySearchCondDto partySearchCondDto, Pageable pageable) {
        List<Party> content = queryFactory
                .select(party)
                .from(party)
                .where(titleLike(partySearchCondDto.getPartyTitle()), menuLike(partySearchCondDto.getMenu()), stateEq(partySearchCondDto.getPartyStatus()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(partySort(pageable))
                .fetch();
        return content;
    }

    private Long getCount(PartySearchCondDto partySearchCondDto) {
        Long count = queryFactory
                .select(party.count())
                .from(party)
                .where(
                        party.partyTitle.eq(partySearchCondDto.getPartyTitle())
                )
                .fetchOne();
        return count;
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? party.partyTitle.contains(title) : null;
    }

    private BooleanExpression menuLike(String menu) {
        return StringUtils.hasText(menu) ? party.menu.contains(menu) : null;
    }

    private BooleanExpression stateEq(PartyStatus partyStatus) {
        if (ObjectUtils.isEmpty(partyStatus)) {
            return null;
        }
        return party.status.eq(partyStatus);
    }

    private OrderSpecifier<?> partySort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
                    case "hit": //조회순
                        return new OrderSpecifier(direction, party.hit);
                    case "Latest": //최신순(생성순)
                        return new OrderSpecifier(direction, party.createDate);
                    case "deadline": //마감순
                        return new OrderSpecifier(direction, party.partyDeadline);
                }
            }
        }
        return new OrderSpecifier(Order.ASC, NullExpression.DEFAULT, OrderSpecifier.NullHandling.Default);
    }
}
