package com.kr.matitting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_history_id")
    private Long id;

    @Column(length = 200, nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true) // 수정필요
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = true) // nullable 수정필요
    private ChatRoom targetRoom;

    @CreatedDate
    @Column(name = "create_date")
    private LocalDateTime createDate;
}
