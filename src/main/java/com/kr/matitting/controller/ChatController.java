package com.kr.matitting.controller;

import com.kr.matitting.dto.ChatEvictDto;
import com.kr.matitting.dto.ChatMessage;
import com.kr.matitting.entity.User;
import com.kr.matitting.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.kr.matitting.dto.ChatRoomDto.ChatResponse;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "채팅 기록 가져오기", description = "채팅 기록 조회 API \n\n" +
                                                        "[로직 설명] \n\n" +
                                                        "채팅방 id에 해당하는 채팅방의 채팅 기록을 조회하여 리턴합니다.")
    @GetMapping("/{roomId}")
    private ResponseEntity<ChatResponse> getChats(@PathVariable Long roomId,
                                                  @AuthenticationPrincipal User user,
                                                  @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
                                                  @RequestParam(value = "lastChatId") Long lastChatId) {
        PageRequest pageRequest = PageRequest.of(0, size, Sort.Direction.DESC, "createDate");
        return ResponseEntity.ok(new ChatResponse(chatService.getChats(user.getId(), roomId, lastChatId,pageRequest)));
    }

    @Operation(summary = "채팅 메시지 전송", description = "WebSocket을 통한 채팅 메시지 전송 API")
    @MessageMapping("/chat/message")
    public void message(ChatMessage chatMessage, @AuthenticationPrincipal User user) {
        chatService.sendMessage(user.getId(), chatMessage);
    }

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
