package com.kr.matitting.config;

import com.kr.matitting.dto.ChatDto;
import com.kr.matitting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import static com.kr.matitting.dto.ChatDto.*;

@Component
@RequiredArgsConstructor
public class IntialDataTemp implements ApplicationRunner {
    private final ChatService chatService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        chatService.createRoomEvent(new CreateRoomEvent(null, null));
        chatService.createRoomEvent(new CreateRoomEvent(null, null));
    }
}
