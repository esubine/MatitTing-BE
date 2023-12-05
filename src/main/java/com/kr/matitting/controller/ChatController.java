package com.kr.matitting.controller;

import com.kr.matitting.annotaiton.RoomType;
import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.dto.ChatUserDto;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.User;
import com.kr.matitting.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kr.matitting.dto.ChatHistoryDto.*;
import static com.kr.matitting.dto.ChatRoomDto.*;
import static com.kr.matitting.dto.ChatUserDto.*;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "내 채팅방 가져오기")
    @GetMapping(value = {"/group", "/1on1"})
    public ResponseEntity<ChatResponse> getAllRooms(@RoomType ChatRoomType roomType,
                                                    @AuthenticationPrincipal User user,
                                                    @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
                                                    @RequestParam(value = "lastChatRoomId") Long lastChatRoomId) {
        PageRequest pageRequest = PageRequest.of(0, size, Sort.Direction.DESC, "createDate");
        return ResponseEntity.ok(new ChatResponse(chatService.getChatRooms(user.getId(), roomType, lastChatRoomId, pageRequest)));
    }

    @Operation(summary = "채팅 기록 가져오기")
    @GetMapping("/history/{roomId}")
    private ResponseEntity<ChatResponse> getHistories(@PathVariable Long roomId,
                                                      @AuthenticationPrincipal User user,
                                                      @RequestParam(value = "size", defaultValue = "5", required = false) Integer size,
                                                      @RequestParam(value = "lastHistoryId") Long lastHistoryId) {
        PageRequest pageRequest = PageRequest.of(0, size, Sort.Direction.DESC, "createDate");
        return ResponseEntity.ok(new ChatResponse(chatService.getHistories(user.getId(), roomId, lastHistoryId,pageRequest)));
    }

    @Operation(summary = "1대1 채팅방 생성")
    @PostMapping("/1one1")
    public void requestOneOnOne(@AuthenticationPrincipal User user, Long partyId) {
        chatService.requestOneOnOne(user.getId(), partyId);
    }

    @Operation(summary = "내 채팅방 이름으로 검색")
    @PostMapping("/search")
    public ResponseEntity<ChatResponse> searchRooms(@RequestParam(name = "name") String title, @AuthenticationPrincipal User user) {
        List<ChatRoomItem> chatRoomItems = chatService.searchChatRoom(user.getId(), title);

        return ResponseEntity.ok(new ChatResponse(chatRoomItems));
    }

    @Operation(summary = "유저 강퇴")
    @DeleteMapping("/{roomId}")
    public void evictUser(@PathVariable Long roomId, @RequestBody ChatEvict evict, @AuthenticationPrincipal User user) { // TODO : targetId DTO로 받아야함
        chatService.evictUser(user.getId(), evict.getTargetId(), roomId);
    }

    @Operation(summary = "채팅방 유저 전부 조회")
    @GetMapping("/{roomId}")
    public ResponseEntity<ChatUserInfoResponse> getUserInfos(@PathVariable Long roomId, @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(chatService.getRoomUsers(roomId, user.getId()));
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessage chatMessage, @AuthenticationPrincipal User user) {
        chatService.sendMessage(user.getId(), chatMessage);
    }

    @Operation(hidden = true)
    @GetMapping("/rooms") // TODO : 임시 테스트용
    public List<ChatRoomItem> getAllRooms() {
        List<ChatRoom> all = chatService.findAll();
        return all.stream()
                .map(ChatRoomItem::new)
                .toList();
    }
}
