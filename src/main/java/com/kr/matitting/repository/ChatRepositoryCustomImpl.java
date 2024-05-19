package com.kr.matitting.repository;

import com.kr.matitting.dto.ResponseChatDto;
import com.kr.matitting.dto.ResponseChatListDto;
import com.kr.matitting.dto.ResponsePageInfoDto;
import com.kr.matitting.entity.Chat;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.kr.matitting.entity.QChat.chat;

@Repository
@RequiredArgsConstructor
public class ChatRepositoryCustomImpl {
    private final JPAQueryFactory queryFactory;

    public ResponseChatListDto getChatList(Long roomId, Pageable pageable) {

        List<Chat> chatList = queryFactory
                .select(chat)
                .from(chat)
                .where(chat.chatRoom.id.eq(roomId))
                .orderBy(chat.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Page<Chat> chatPage = new PageImpl<>(chatList, pageable, chatList.size());

        List<ResponseChatDto> responseChatDtos = chatList.stream()
                .map(chat -> ResponseChatDto.builder()
                        .chatId(chat.getId())
                        .senderId(chat.getSendUser().getId())
                        .nickname(chat.getSendUser().getNickname())
                        .message(chat.getMessage())
                        .imgUrl(chat.getSendUser().getUser().getImgUrl())
                        .createAt(chat.getCreateDate())
                        .build())
                .toList();

        ResponsePageInfoDto pageInfo = new ResponsePageInfoDto(pageable.getPageNumber(), chatPage.hasNext());

        return new ResponseChatListDto(responseChatDtos, pageInfo);
    }

}
