package com.kr.matitting.entity;

import com.kr.matitting.constant.ChatRoomType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "chat_room")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToOne
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User master;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> chatUserList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chatList = new ArrayList<>();

    public static ChatRoom createRoom(Party party, User user, String title) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.party = party;
        chatRoom.master = user;
        chatRoom.setModifiedDate(LocalDateTime.now());
        chatRoom.title = title;

        return chatRoom;
    }
}
