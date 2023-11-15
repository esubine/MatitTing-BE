package com.kr.matitting.repository;

import com.kr.matitting.entity.ChatHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    @Query("select ch from ChatHistory ch " +
            "join fetch ch.chatUser " +
            "where ch.chatUser.id = :chatUserId")
    List<ChatHistory> findByChatUserIdFJChatUser(@Param("chatUserId") Long chatUserId, Pageable pageable);
}
