package com.kr.matitting.service;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.entity.*;
import com.kr.matitting.repository.ChatHistoryRepository;
import com.kr.matitting.repository.ChatRoomRepository;
import com.kr.matitting.repository.ChatUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

import static com.kr.matitting.constant.ChatRoomType.*;
import static com.kr.matitting.dto.ChatDto.*;
import static com.kr.matitting.dto.ChatHistoryDto.*;
import static com.kr.matitting.dto.ChatRoomDto.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createRoomEvent(CreateRoomEvent event) {
        ChatRoom room = createPartyChat(event);
//        chatRoomRepository.findByPartyId(event.getParty().getId()).ifPresent(
//                party -> {
//                    throw new RuntimeException();
//                }
//        );
        chatRoomRepository.save(room);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createRoomEvent(CreatePrivateRoomEvent event) {
        ChatRoom room = createPartyChat(event);
        chatRoomRepository.findByPartyId(event.getParty().getId()).ifPresent(
                party -> {
                    throw new RuntimeException();
                }
        );
        chatRoomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> searchChatRooms(String title, Pageable pageable) {
        List<ChatRoom> rooms = chatRoomRepository.findByTitleStartsWithAndChatRoomTypeOrderByCreateDateDesc(title, GROUP, pageable);

        return convertRoomToDto(rooms);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomResponseDto> getRooms(Long userId, ChatRoomType chatRoomType, Pageable pageable) {
        List<ChatUser> chatUsers = chatUserRepository.findByUserId(userId)
                .orElse(null);

        if (chatUsers == null) {
            return null;
        }

        List<ChatRoom> rooms = chatRoomRepository.findByOwnerInAndChatRoomTypeOrderByCreateDateDesc(chatUsers, chatRoomType, pageable);

        return convertRoomToDto(rooms);
    }

    @Transactional(readOnly = true)
    public List<ChatHistoryResponseDto> getHistories(User user, Long roomId, Pageable pageable) {
        // TODO: Exception Handling
        ChatUser chatUser = chatUserRepository.findByUserIdAndAndChatRoomId(user.getId(), roomId).orElseThrow();
        List<ChatHistory> chatHistories = chatHistoryRepository.findByTargetRoomIdOrderByCreateDateDesc(roomId, pageable);

        return convertChatHistoriesToDto(chatHistories);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        chatRoomRepository.deleteById(roomId);
    }

    @Transactional
    public void saveRoomHistory() {
        throw new RuntimeException();
    }

    private List<ChatRoomResponseDto> convertRoomToDto(List<ChatRoom> rooms) {
        if (rooms == null) {
            return null;
        }

        return rooms.stream()
                .map(ChatRoomResponseDto::of)
                .toList();
    }

    private List<ChatHistoryResponseDto> convertChatHistoriesToDto(List<ChatHistory> histories) {
        if (histories == null) {
            return null;
        }

        return histories
                .stream()
                .map(ChatHistoryResponseDto::of)
                .toList();
    }

    private ChatRoom createPartyChat(CreateRoomEvent event) {
//        return null;
        return ChatRoom.createRoom(UUID.randomUUID().toString(), event.getParty(), GROUP, null);
    }

    private ChatRoom createPartyChat(CreatePrivateRoomEvent event) {
        return null;
//        return ChatRoom.createRoom(event.getParty().getPartyTitle(), event.getParty(), GROUP, event.getOwner());
    }
}
