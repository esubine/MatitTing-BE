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
        String receiver;
        String sender;
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
                    .receiver(notification.getReceiver().getNickname())
                    .sender(notification.getSender().getNickname())
                    .type(notification.getNotificationType())
                    .createdAt(notification.getCreateDate().toString())
                    .lastEventId(lastEventId)
                    .partyId(party.getId())
                    .hostId(party.getUser().getId())
                    .build();
        }

        public static Response createResponse(Notification notification) {
            return Response.builder()
                    .title(notification.getTitle())
                    .content(notification.getContent())
                    .id(notification.getId().toString())
                    .receiver(notification.getReceiver().getNickname())
                    .sender(notification.getSender().getNickname())
                    .type(notification.getNotificationType())
                    .createdAt(notification.getCreateDate().toString())
                    .partyId(notification.getParty().getId())
                    .hostId(notification.getParty().getUser().getId())
                    .build();
        }
    }
}