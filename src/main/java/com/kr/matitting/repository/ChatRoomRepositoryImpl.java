package com.kr.matitting.repository;

import com.kr.matitting.dto.ResponseChatRoomDto;
import com.kr.matitting.dto.ResponseChatRoomListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.Chat;
import com.kr.matitting.entity.ChatRoom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QChat.chat;
import static com.kr.matitting.entity.QChatRoom.chatRoom;
import static com.kr.matitting.entity.QChatUser.chatUser;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public ResponseChatRoomListDto getChatRooms(Long userId, Pageable pageable) {

        JPAQuery<ChatRoom> query = queryFactory
                .select(chatRoom)
                .from(chatUser)
                .join(chatUser.chatRoom, chatRoom)
                .where(chatUser.user.id.eq(userId))
                .orderBy(chatRoom.modifiedDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<ChatRoom> chatRooms = query.fetch();

        long totalCount = getChatCount(userId);

        Page<ChatRoom> chatRoomsPage = new PageImpl<>(chatRooms, pageable, totalCount);

        List<ResponseChatRoomDto> responseChatRoomDtos = chatRooms.stream()
                .map(chatRoom -> ResponseChatRoomDto.builder()
                        .roomId(chatRoom.getId())
                        .title(chatRoom.getTitle())
                        .thumbnail(chatRoom.getParty().getThumbnail())
                        .lastMessage(findLastMessage(chatRoom) == null ? null : findLastMessage(chatRoom).getMessage())
                        .lastMessageTime(findLastMessage(chatRoom) == null ? null : findLastMessage(chatRoom).getCreateDate())
                        .lastUpdate(chatRoom.getModifiedDate())
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

