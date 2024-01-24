package com.kr.matitting.repository;

import com.kr.matitting.entity.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select ch from Chat ch " +
            "join fetch ch.chatUser " +
            "where ch.chatUser.id = :chatUserId")
    List<Chat> findByChatUserIdFJChatUser(@Param("chatUserId") Long chatUserId, Pageable pageable);
}
