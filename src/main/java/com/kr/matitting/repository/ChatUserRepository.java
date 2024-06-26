package com.kr.matitting.repository;

import com.kr.matitting.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUser, Long> {
    Optional<ChatUser> findByUserIdAndChatRoomId(Long userId, Long roomId);
    List<ChatUser> findByChatRoomId(Long roomId);
    List<ChatUser> findByUserId(Long userId);
}
