package com.kr.matitting.service;

import com.kr.matitting.constant.NotificationType;
import com.kr.matitting.dto.NotificationDto;
import com.kr.matitting.entity.Notification;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.EmitterRepository;
import com.kr.matitting.repository.EmitterRepositoryImpl;
import com.kr.matitting.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final EmitterRepositoryImpl emitterRepositoryImpl;

    public SseEmitter subscribe(User user, String lastEventId) {

        String emitterId = makeTimeIncludeId(user);

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        log.info("new emitter added : {}", emitter);
        log.info("lastEventId : {}", lastEventId);

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        String eventId = makeTimeIncludeId(user);
        //503 에러 방지를 위한 더미 이벤트 전송
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + user.getId() + "]");

        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, user, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(User user) {
        return user.getId() + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, User user, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(user.getId()));
        System.out.println("eventCaches = " + eventCaches.size());

//        Map<String, SseEmitter> emitters = emitterRepositoryImpl.getEmitters();
//        Map<String, Object> eventCache = emitterRepositoryImpl.getEventCache();

// 뭔가 이상하다! 캐시아이디가 나와야하는데 이미터아이디가 eventCaches의 id로 나오고 있음
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) == 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void send(User receiver, NotificationType notificationType, String content){
        Notification notification = notificationRepository.save(new Notification(receiver, notificationType, content));

        Long userId = receiver.getId();
        String eventId = userId+"_"+System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(userId));

        System.out.println("eventId = " + eventId);

        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, NotificationDto.Response.createResponse(notification, eventId));
                }
        );
    }
}
