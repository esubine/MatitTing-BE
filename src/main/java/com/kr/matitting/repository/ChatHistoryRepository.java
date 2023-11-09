package com.kr.matitting.repository;

import com.kr.matitting.entity.ChatHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findByTargetRoomIdOrderByCreateDateDesc(Long targetRoomId, Pageable pageable);
}
