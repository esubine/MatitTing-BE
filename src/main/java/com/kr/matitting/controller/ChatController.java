package com.kr.matitting.controller;

import com.kr.matitting.annotaiton.RoomType;
import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.dto.ChatUserDto;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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

    private Long userId = 1L; // TODO: 임시, 이부분은 시큐리티 수정후 반영 필요

    @GetMapping(value = {"/group", "/1on1"})
    public ResponseEntity<ChatResponse> getAllRooms(@RoomType ChatRoomType roomType,
                                                    @RequestParam(value = "offset", defaultValue = "1", required = false) Integer offset,
                                                    @RequestParam(value = "limit", defaultValue = "5", required = false) Integer limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.Direction.DESC, "createDate");
        return ResponseEntity.ok(new ChatResponse(chatService.getChatRooms(userId, roomType, pageRequest)));
    }

    @GetMapping("/history/{roomId}")
    private ResponseEntity<ChatResponse> getHistories(@PathVariable Long roomId,
                                                      @RequestParam(value = "offset", defaultValue = "1", required = false) Integer offset,
                                                      @RequestParam(value = "limit", defaultValue = "5", required = false) Integer limit) {
        PageRequest pageRequest = PageRequest.of(offset, limit, Sort.Direction.DESC, "createDate");
        return ResponseEntity.ok(new ChatResponse(chatService.getHistories(userId, roomId, pageRequest)));
    }

    @PostMapping
    public void requestOneOnOne(Long partyId) {
        chatService.requestOneOnOne(userId, partyId);
    }

    @PostMapping("/search")
    public ResponseEntity<ChatResponse> searchRooms(@RequestParam(name = "name") String title) {
        List<ChatRoomItem> chatRoomItems = chatService.searchChatRoom(userId, title);

        return ResponseEntity.ok(new ChatResponse(chatRoomItems));
    }

    @DeleteMapping("/{roomId}")
    public void evictUser(@PathVariable Long roomId, @RequestBody ChatEvict evict) { // TODO : targetId DTO로 받아야함
        chatService.evictUser(userId, evict.getTargetId(), roomId);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatUserInfoResponse> getUserInfos(@PathVariable Long roomId) {
        return ResponseEntity.ok(chatService.getRoomUsers(roomId, userId));
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessage chatMessage) {
        chatService.sendMessage(userId, chatMessage);
    }

    @GetMapping("/rooms") // TODO : 임시 테스트용
    public List<ChatRoomItem> getAllRooms() {
        List<ChatRoom> all = chatService.findAll();
        return all.stream()
                .map(ChatRoomItem::new)
                .toList();
    }
}
