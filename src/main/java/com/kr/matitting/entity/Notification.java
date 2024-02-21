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
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;
    @Builder
    public Notification(User receiver, NotificationType notificationType, String title, String content) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.title = title;
        this.content = content;
    }
}