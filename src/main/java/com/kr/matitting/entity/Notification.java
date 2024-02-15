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
    private String content;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private User receiver;
    @Builder
    public Notification(User receiver, NotificationType notificationType, String content) {
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.content = content;
    }
}