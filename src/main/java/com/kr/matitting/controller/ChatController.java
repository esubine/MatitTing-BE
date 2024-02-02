package com.kr.matitting.controller;

import com.kr.matitting.dto.ChatEvictDto;
import com.kr.matitting.dto.ChatMessage;
import com.kr.matitting.entity.ChatRoom;
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

import java.util.List;

import static com.kr.matitting.dto.ChatRoomDto.ChatResponse;
import static com.kr.matitting.dto.ChatRoomDto.ChatRoomItem;

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

//    @Operation(summary = "유저 채팅방 나가기")
//    @DeleteMapping("/exit/{roomId}")
//    public void exitChatRoom(@PathVariable Long roomId,
//                          @AuthenticationPrincipal User user) {
//        chatService.exitChatRoom(user.getId(), roomId);
//    }


//    @Operation(summary = "내 채팅방 이름으로 검색")
//    @PostMapping("/search")
//    public ResponseEntity<ChatResponse> searchRooms(@RequestParam(name = "name") String title, @AuthenticationPrincipal User user) {
//        List<ChatRoomItem> chatRoomItems = chatService.searchChatRoom(user.getId(), title);
//
//        return ResponseEntity.ok(new ChatResponse(chatRoomItems));
//    }
}
