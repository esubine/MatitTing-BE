package com.kr.matitting.entity;

import com.kr.matitting.constant.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatUser extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_user_id")
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role userRole;

    public ChatUser(ChatRoom chatRoom, User user, Role userRole) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.userRole = userRole;
        this.nickname = user.getNickname();
    }
}

