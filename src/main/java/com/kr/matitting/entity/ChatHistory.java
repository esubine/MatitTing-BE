package com.kr.matitting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "chat_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_history")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_user_id")
    private ChatUser chatUser;

    @Column(nullable = false, length = 100)
    private String content;

    @CreatedDate
    private LocalDateTime createDate;

    public static ChatHistory createHistory(ChatUser chatUser, String content) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.chatUser = chatUser;
        chatHistory.content = content;

        return chatHistory;
    }
}
