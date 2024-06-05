package com.kr.matitting.repository;

import com.kr.matitting.dto.ResponseChatRoomDto;
import com.kr.matitting.dto.ResponseChatRoomListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.*;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.kr.matitting.entity.QChat.chat;
import static com.kr.matitting.entity.QChatRoom.chatRoom;
import static com.kr.matitting.entity.QChatUser.chatUser;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public ResponseChatRoomListDto getChatRooms(Long userId, Pageable pageable) {

        // 최신 메시지 생성 시간을 기준으로 채팅방 정렬
        JPAQuery<ChatRoom> query = queryFactory
                .select(chatRoom)
                .from(chatRoom)
                .join(chatRoom.chatUserList, chatUser)
                .leftJoin(chat).on(chat.chatRoom.eq(chatRoom))
                .where(chatUser.user.id.eq(userId))
                .groupBy(chatRoom.id)
                .orderBy(chat.createDate.max().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<ChatRoom> chatRooms = query.fetch();
        long totalCount = getChatCount(userId);

        Page<ChatRoom> chatRoomsPage = new PageImpl<>(chatRooms, pageable, totalCount);

        List<ResponseChatRoomDto> responseChatRoomDtos = chatRooms.stream()
                .map(chatRoomDto -> ResponseChatRoomDto.builder()
                        .roomId(chatRoomDto.getId())
                        .title(chatRoomDto.getTitle())
                        .thumbnail(chatRoomDto.getParty().getThumbnail())
                        .lastMessage(findLastMessage(chatRoomDto) == null ? null : findLastMessage(chatRoomDto).getMessage())
                        .lastMessageTime(findLastMessage(chatRoomDto) == null ? null : findLastMessage(chatRoomDto).getCreateDate())
                        .lastUpdate(chatRoomDto.getModifiedDate())
                        .build())
                .toList();

        ResponsePageInfoDto pageInfoDto = new ResponsePageInfoDto(pageable.getPageNumber(), chatRoomsPage.hasNext() );

        return new ResponseChatRoomListDto(responseChatRoomDtos, pageInfoDto);
    }

    public ResponseChatRoomListDto getChatRoomsByTitleSearch(Long userId, Pageable pageable, String searchTitle) {

        JPAQuery<ChatRoom> query = queryFactory
                .select(chatRoom)
                .from(chatRoom)
                .join(chatRoom.chatUserList, chatUser)
                .leftJoin(chat).on(chat.chatRoom.eq(chatRoom))
                .where(chatUser.user.id.eq(userId).and(chatRoom.title.containsIgnoreCase(searchTitle)))
                .groupBy(chatRoom.id)
                .orderBy(chat.createDate.max().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<ChatRoom> chatRooms = query.fetch();
        long totalCount = getChatCount(userId);

        Page<ChatRoom> chatRoomsPage = new PageImpl<>(chatRooms, pageable, totalCount);

        List<ResponseChatRoomDto> responseChatRoomDtos = chatRooms.stream()
                .map(chatRoomDto -> ResponseChatRoomDto.builder()
                        .roomId(chatRoomDto.getId())
                        .title(chatRoomDto.getTitle())
                        .thumbnail(chatRoomDto.getParty().getThumbnail())
                        .lastMessage(findLastMessage(chatRoomDto) == null ? null : findLastMessage(chatRoomDto).getMessage())
                        .lastMessageTime(findLastMessage(chatRoomDto) == null ? null : findLastMessage(chatRoomDto).getCreateDate())
                        .lastUpdate(chatRoomDto.getModifiedDate())
                        .build())
                .toList();

        ResponsePageInfoDto pageInfoDto = new ResponsePageInfoDto(pageable.getPageNumber(), chatRoomsPage.hasNext() );

        return new ResponseChatRoomListDto(responseChatRoomDtos, pageInfoDto);
    }

    private Chat findLastMessage(ChatRoom chatRoom) {
        return queryFactory
                .select(chat)
                .from(chat)
                .where(chat.chatRoom.eq(chatRoom))
                .orderBy(chat.createDate.desc())
                .fetchFirst();
    }

    private Long getChatCount(Long userId) {
        return queryFactory
                .select(chatRoom.count())
                .from(chatUser)
                .join(chatUser.chatRoom, chatRoom)
                .where(chatUser.user.id.eq(userId))
                .fetchOne();
    }
}

