package com.kr.matitting.controller;

import com.kr.matitting.dto.ChatEvictDto;
import com.kr.matitting.dto.ChatMessageDto;
import com.kr.matitting.dto.ResponseChatListDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "채팅 기록 가져오기", description = "채팅 기록 조회 API \n\n" +
            "[로직 설명] \n\n" +
            "채팅방 id에 해당하는 채팅방의 채팅 기록을 조회하여 리턴합니다.")
    @GetMapping("/{roomId}")
    public ResponseEntity<ResponseChatListDto> getChats(@PathVariable Long roomId,
                                                        @AuthenticationPrincipal User user,
                                                        @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
                                                        @RequestParam(value = "lastChatId") Long lastChatId) {
        PageRequest pageable = PageRequest.of(0, size);
        ResponseChatListDto responseChatListDto = chatService.getChats(user.getId(), roomId, lastChatId, pageable);
        return ResponseEntity.ok(responseChatListDto);
    }

    //채팅 메시지 전송
    @Operation(summary = "채팅 메시지 전송", description = "메시지 전송 API \n\n" +
                                                        "")
    @MessageMapping("/message")
    public void message(ChatMessageDto chatMessageDto, @AuthenticationPrincipal User user) {
        chatService.sendMessage(user, chatMessageDto);
    }

    //유저 강제 퇴장
    @Operation(summary = "유저 강퇴 API", description = "유저 강퇴 API \n\n" +
            "[로직 설명] \n\n" +
            "1. 강퇴할 유저의 id를 request합니다. \n\n" +
            "2. 요청자가 HOST인 경우에만 유저를 강퇴할 수 있습니다.")
    @DeleteMapping("/{roomId}")
    public void evictUser(@PathVariable Long roomId,
                          @RequestBody ChatEvictDto chatEvictDto,
                          @AuthenticationPrincipal User user) {
        chatService.evictUser(user.getId(), chatEvictDto.getTargetId(), roomId);
    }
}
