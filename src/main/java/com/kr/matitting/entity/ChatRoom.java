package com.kr.matitting.entity;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.constant.ChatUserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")
    private Party party;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ChatRoomType chatRoomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_user_id", nullable = true) //TODO: 임시
    private ChatUser owner;

    public static ChatRoom createRoom(String title, Party party, ChatRoomType chatRoomType, ChatUser owner) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.title = title;
        chatRoom.party = party;
        chatRoom.chatRoomType = chatRoomType;
        chatRoom.owner = owner;

        return chatRoom;
    }
}
