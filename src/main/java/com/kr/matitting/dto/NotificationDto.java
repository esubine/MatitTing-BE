package com.kr.matitting.dto;

import com.kr.matitting.constant.NotificationType;
import com.kr.matitting.entity.Notification;
import lombok.*;

public class NotificationDto {
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        String id;
        String name;
        String content;
        NotificationType type;
        String createdAt;

        String lastEventId;
        public static Response createResponse(Notification notification, String lastEventId) {
            return Response.builder()
                    .content(notification.getContent())
                    .id(notification.getId().toString())
                    .name(notification.getReceiver().getNickname())
                    .type(notification.getNotificationType())
                    .createdAt(notification.getCreateDate().toString())
                    .lastEventId(lastEventId)
                    .build();
        }
    }
}
