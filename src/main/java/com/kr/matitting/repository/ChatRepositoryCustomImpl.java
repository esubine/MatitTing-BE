package com.kr.matitting.repository;

import com.kr.matitting.dto.ResponseChatDto;
import com.kr.matitting.dto.ResponseChatListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.Chat;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QChat.chat;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryCustomImpl {
    private final JPAQueryFactory queryFactory;

    public ResponseChatListDto getChatList(Long roomId, Pageable pageable, Long lastChatId){

        JPAQuery<Chat> query = queryFactory
                .select(chat)
                .from(chat)
                .where(chat.chatRoom.id.eq(roomId),
                        chat.id.lt(lastChatId))
                .orderBy(chat.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()+1);

        List<Chat> chatList = query.fetch();

        List<ResponseChatDto> responseChatDtos = chatList.stream()
                .map(chat -> ResponseChatDto.builder()
                        .chatId(chat.getId())
                        .senderId(chat.getSendUser().getId())
                        .message(chat.getMessage())
                        .createAt(chat.getCreateDate())
                        .build())
                .toList();
        ResponsePageInfoDto pageInfo = checkLastChat(chatList, responseChatDtos, pageable);

        return new ResponseChatListDto(responseChatDtos, pageInfo);
    }

    private ResponsePageInfoDto checkLastChat(List<Chat> chats, List<ResponseChatDto> ResponseChatDtos, Pageable pageable) {

        boolean hasNext = chats.size() > pageable.getPageSize();
        Long lastChatId = chats.isEmpty() ? null : ResponseChatDtos.get(chats.size() - 1).getChatId();

        return new ResponsePageInfoDto(lastChatId, hasNext);

    }

}
