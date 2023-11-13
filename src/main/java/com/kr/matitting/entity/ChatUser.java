package com.kr.matitting.entity;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.constant.ChatUserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatUser extends BaseTimeEntity{
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
    private ChatUserRole userRole;

    @Enumerated(EnumType.STRING)
    private ChatRoomType roomType;

    public static ChatUser createChatUser(ChatRoom chatRoom, User user, ChatUserRole userRole, ChatRoomType roomType) {
        ChatUser chatUser = new ChatUser();
        chatUser.chatRoom = chatRoom;
        chatUser.user = user;
        chatUser.userRole = userRole;
        chatUser.roomType = roomType;
        chatUser.nickname = user.getNickname();
        return chatUser;
    }
}
