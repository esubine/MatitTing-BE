package com.kr.matitting.repository;

import com.kr.matitting.dto.ResponseChatRoomDto;
import com.kr.matitting.dto.ResponseChatRoomListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.*;
import com.kr.matitting.exception.chat.ChatException;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kr.matitting.entity.QChatRoom.chatRoom;
import static com.kr.matitting.entity.QChatUser.chatUser;
import static com.kr.matitting.entity.QParty.party;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public ResponseChatRoomListDto getChatRooms(Long userId, LocalDateTime time, Pageable pageable) {

        JPAQuery<ChatRoom> query = queryFactory
                .select(chatRoom)
                .from(chatUser)
                .where(chatUser.user.id.eq(userId))
                .orderBy(chatRoom.modifiedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1);

        if (time != null) {
            query = query.where(chatRoom.modifiedDate.lt(time));
        }

        List<ChatRoom> chatRooms = query.fetch();

        List<ResponseChatRoomDto> responseChatRoomDtos = chatRooms.stream()
                .map(chatRoom -> ResponseChatRoomDto.builder()
                        .roomId(chatRoom.getId())
                        .title(chatRoom.getTitle())
                        .lastUpdate(chatRoom.getModifiedDate())
                        .build())
                .toList();

        ResponsePageInfoDto pageInfo = checkLastPage(chatRooms, responseChatRoomDtos, pageable);

        return new ResponseChatRoomListDto(responseChatRoomDtos, pageInfo);
    }

    private ResponsePageInfoDto checkLastPage(List<ChatRoom> chatRooms, List<ResponseChatRoomDto> responseChatRoomDtos, Pageable pageable) {

        boolean hasNext = chatRooms.size() > pageable.getPageSize();
        Long lastPartyId = chatRooms.isEmpty() ? null : responseChatRoomDtos.get(chatRooms.size() - 1).getRoomId();

        return new ResponsePageInfoDto(lastPartyId, hasNext);

    }
}

