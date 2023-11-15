package com.kr.matitting.repository;

import com.kr.matitting.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByUserIdAndTitleLike(Long userId, String title);
}
