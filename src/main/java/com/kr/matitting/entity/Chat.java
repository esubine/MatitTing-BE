package com.kr.matitting.entity;

import com.kr.matitting.constant.MessageType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "chat")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_user_id")
    private ChatUser sendUser;

    @Column(nullable = false, length = 100)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 100)
    private MessageType messageType;

    public Chat(ChatUser chatUser, ChatRoom chatRoom, String message, MessageType messageType){
        this.sendUser = chatUser;
        this.message = message;
        this.chatRoom = chatRoom;
        this.messageType = messageType;
    }

}
