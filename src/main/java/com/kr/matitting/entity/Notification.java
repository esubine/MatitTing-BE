package com.kr.matitting.entity;

import com.kr.matitting.constant.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;
    private String eventId;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Builder
    public Notification(User receiver, User sender, NotificationType notificationType, String title, String content, String eventId) {
        this.receiver = receiver;
        this.sender = sender;
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
        this.eventId = eventId;
    }
}