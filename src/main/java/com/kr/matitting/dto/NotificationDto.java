package com.kr.matitting.dto;

import com.kr.matitting.constant.NotificationType;
import com.kr.matitting.entity.Notification;
import com.kr.matitting.entity.Party;
import lombok.*;

import java.time.LocalDate;

public class NotificationDto {
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Response {
        String id;
        String name;
        String title;
        String content;
        NotificationType type;
        String createdAt;
        String lastEventId;
        Long partyId;
        Long hostId;

        public static Response createResponse(Notification notification, Party party, String lastEventId) {
            return Response.builder()
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .id(notification.getId().toString())
                    .name(notification.getReceiver().getNickname())
                    .type(notification.getNotificationType())
                    .createdAt(LocalDate.from(notification.getCreateDate()).toString())
                    .lastEventId(lastEventId)
                    .partyId(party.getId())
                    .hostId(party.getUser().getId())
                    .build();
        }
    }
}