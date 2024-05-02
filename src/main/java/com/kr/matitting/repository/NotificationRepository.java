package com.kr.matitting.repository;

import com.kr.matitting.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByReceiver_Id(Long receiverId);
}