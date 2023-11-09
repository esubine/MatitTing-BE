package com.kr.matitting.controller;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.dto.ChatDto;
import com.kr.matitting.dto.ChatHistoryDto;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.repository.ChatRoomRepository;
import com.kr.matitting.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kr.matitting.dto.ChatDto.*;
import static com.kr.matitting.dto.ChatDto.ChatMessage.MessageType.*;
import static com.kr.matitting.dto.ChatHistoryDto.*;
import static com.kr.matitting.dto.ChatRoomDto.*;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatRoomRepository chatRoomRepository; // TODO: 삭제

    @GetMapping(value = {"/1on1", "/group"})
    public ResponseEntity<List<ChatRoomResponseDto>> getRooms(
            HttpServletRequest request,
            @RequestBody ChatRoomRequestDto chatRoomRequestDto,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {

        Pageable descPageable = createDescPageable(offset, limit);

        return ok().body(chatService.getRooms(chatRoomRequestDto.getUserId(), resolveChatRoomType(request), descPageable));
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomResponseDto>> searchRooms(
            @RequestParam String title,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {

        Pageable descPageable = createDescPageable(offset, limit);

        return ok().body(chatService.searchChatRooms(title, descPageable));
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatHistoryResponseDto>> getHistories(
            @PathVariable Long chatRoomId,
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "limit", required = false, defaultValue = "5") Integer limit) {

        Pageable descPageable = createDescPageable(offset, limit);

        return ok().body(chatService.getHistories(null, chatRoomId, descPageable));
    }

    @MessageMapping("/chat/message")
    public void message(ChatMessage chatMessage) {
        if(chatMessage.getType().equals(ENTER)) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장 하셨습니다.");
        } else if(chatMessage.getType().equals(OUT)) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장 하셨습니다.");
        }
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    @GetMapping("/chat/rooms")
    public List<ChatRoom> getAllRooms() {
        List<ChatRoom> all = chatRoomRepository.findAll();
        return all;
    }

    // TODO : Argument Resolve
    private ChatRoomType resolveChatRoomType(HttpServletRequest request) {
        return request.getRequestURI().contains("1on1") ? ChatRoomType.PRIVATE : ChatRoomType.GROUP;
    }

    private Pageable createDescPageable(Integer offset, Integer limit) {
        return PageRequest.of(offset, limit, Sort.by("createdAt").descending());
    }
}
