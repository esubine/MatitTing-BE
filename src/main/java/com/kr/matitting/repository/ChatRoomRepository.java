package com.kr.matitting.repository;

import com.kr.matitting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByPartyId(Long partyId);

//    List<ChatRoom> findByUserIdAndTitleLike(Long userId, String title);
//
//    Optional<ChatRoom> findByPartyIdAndRoomType(Long partyId, ChatRoomType type);
}
