package com.kr.matitting.repository;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.entity.Chat;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.QChatHistory;
import com.kr.matitting.entity.QChatRoom;
import com.querydsl.core.types.NullExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QParty.party;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryCustomImpl implements ChatRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QChatHistory qChatHistory = QChatHistory.chatHistory;
    private final QChatRoom qChatRoom = QChatRoom.chatRoom;

    public List<Chat> getChats(Long chatUserId, Long roomId, Long lastId, Pageable pageable) {
        return queryFactory
                .selectFrom(qChatHistory)
                .where(
                        qChatHistory.id.lt(lastId),
                        chatUserIdEq(chatUserId),
                        roomIdEq(roomId)
                )
                .leftJoin(
                        qChatHistory.chatUser
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(partySort(pageable))
                .fetch();
    }

    @Override
    public List<ChatRoom> getChatRooms(Long userId, ChatRoomType roomType, Long lastId, Pageable pageable) {
        return queryFactory
                .selectFrom(qChatRoom)
                .where(
                        qChatRoom.id.lt(lastId),
                        qChatRoom.roomType.eq(roomType),
                        qChatRoom.user.id.eq(userId)
                )
                .leftJoin(
                        qChatHistory.chatUser
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(partySort(pageable))
                .fetch();
    }

    private BooleanExpression roomIdEq(Long roomId) {
        return qChatHistory.chatUser.chatRoom.id.eq(roomId);
    }

    private BooleanExpression chatUserIdEq(Long chatUserId) {
        return qChatHistory.chatUser.id.eq(chatUserId);
    }

    private OrderSpecifier<?> partySort(Pageable pageable) {
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;

                switch (order.getProperty()) {
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
