package com.kr.matitting.repository;

import com.kr.matitting.dto.ResponseChatPageInfoDto;
import com.kr.matitting.dto.ResponseChatRoomDto;
import com.kr.matitting.dto.ResponseChatRoomListDto;
import com.kr.matitting.entity.Chat;
import com.kr.matitting.entity.ChatRoom;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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

    public ResponseChatRoomListDto getChatRooms(Long userId, LocalDateTime time, Pageable pageable) {

        JPAQuery<ChatRoom> query = queryFactory
                .select(chatRoom)
                .from(chatUser)
                .where(chatUser.user.id.eq(userId))
                .orderBy(chatRoom.modifiedDate.desc())
                .limit(pageable.getPageSize() + 1);

        if (time != null) {
            query = query.where(chatRoom.modifiedDate.lt(time));
        }

        List<ChatRoom> chatRooms = query.fetch();

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

        ResponseChatPageInfoDto pageInfo = checkLastPage(chatRooms, responseChatRoomDtos, pageable);

        return new ResponseChatRoomListDto(responseChatRoomDtos, pageInfo);
    }

    private ResponseChatPageInfoDto checkLastPage(List<ChatRoom> chatRooms, List<ResponseChatRoomDto> responseChatRoomDtos, Pageable pageable) {

        boolean hasNext = chatRooms.size() > pageable.getPageSize();
        Long lastPartyId = chatRooms.isEmpty() ? null : responseChatRoomDtos.get(chatRooms.size() - 1).getRoomId();

        return new ResponseChatPageInfoDto(lastPartyId, hasNext);

    }

    private Chat findLastMessage(ChatRoom chatRoom) {
        return queryFactory
                .select(chat)
                .from(chat)
                .where(chat.chatRoom.eq(chatRoom))
                .orderBy(chat.createDate.desc())
                .fetchFirst();
    }
}

