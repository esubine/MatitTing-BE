package com.kr.matitting.repository;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.ChatUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByOwnerInAndChatRoomTypeOrderByCreateDateDesc(List<ChatUser> chatUsers, ChatRoomType chatRoomType, Pageable pageable);
    List<ChatRoom> findByTitleStartsWithAndChatRoomTypeOrderByCreateDateDesc(String title, ChatRoomType chatRoomType, Pageable pageable);

    Optional<ChatRoom> findByPartyId(Long partId);
}
