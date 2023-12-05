package com.kr.matitting.repository;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.entity.ChatHistory;
import com.kr.matitting.entity.ChatRoom;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRepositoryCustom {

    List<ChatHistory> getHistories(Long chatUserId, Long roomId, Long lastId, Pageable pageable);

    List<ChatRoom> getChatRooms(Long userId, ChatRoomType roomType, Long lastId, Pageable pageable);
}
