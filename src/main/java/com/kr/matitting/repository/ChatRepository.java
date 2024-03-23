package com.kr.matitting.repository;

import com.kr.matitting.entity.Chat;
import com.kr.matitting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
}
