package com.kr.matitting.service;

import com.kr.matitting.constant.ChatRoomType;
import com.kr.matitting.entity.*;
import com.kr.matitting.exception.chat.ChatException;
import com.kr.matitting.exception.chat.ChatExceptionType;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Optional;

import static com.kr.matitting.constant.ChatRoomType.*;
import static com.kr.matitting.constant.ChatUserRole.*;
import static com.kr.matitting.dto.ChatHistoryDto.*;
import static com.kr.matitting.dto.ChatRoomDto.*;
import static com.kr.matitting.dto.ChatUserDto.*;
import static com.kr.matitting.entity.ChatRoom.*;
import static com.kr.matitting.exception.chat.ChatExceptionType.*;
import static com.kr.matitting.exception.party.PartyExceptionType.*;
import static com.kr.matitting.exception.user.UserExceptionType.*;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatHistoryRepository chatHistoryRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    @Transactional(readOnly = true)
    public List<ChatRoomItem> getChatRooms(Long userId, ChatRoomType roomType, Pageable pageable) {
        List<ChatUser> chatUsers = chatUserRepository.findByUserIdAndRoomTypeFJRoom(userId, roomType, pageable);

        if (chatUsers == null) return null;

        return chatUsers.stream()
                .map(ChatRoomItem::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatHistoryResponse> getHistories(Long userId, Long roomId, Pageable pageable) {
        ChatUser chatUser = chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow();

        List<ChatHistory> chatHistories = chatHistoryRepository.findByChatUserIdFJChatUser(chatUser.getId(), pageable);

        if (chatHistories == null) return null;

        return chatHistories.stream()
                .map(ChatHistoryResponse::new)
                .toList();
    }

    @Transactional
    public void requestOneOnOne(Long userId, Long partyId) {
        Optional<ChatUser> chatRoomOptional = chatUserRepository.findByPartyIdFJChatRoom(partyId);
        Party party = partyRepository.findByIdFJUser(partyId).orElseThrow(
                () -> new PartyException(NOT_FOUND_PARTY)
        );
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserException(NOT_FOUND_USER)
        );

        if (chatRoomOptional.isPresent()) {
            throw new ChatException(NOT_FOUND_CHAT_ROOM);
        }

        ChatRoom room = createRoom(party, user, PRIVATE, party.getPartyTitle());
        chatRoomRepository.save(room);

        ChatUser chatOwner = ChatUser.createChatUser(room, party.getUser(), ADMIN, PRIVATE);
        ChatUser chatParticipant = ChatUser.createChatUser(room, party.getUser(), PARTICIPANT, PRIVATE);

        chatUserRepository.save(chatOwner);
        chatUserRepository.save(chatParticipant);
    }

    @Transactional(readOnly = true)
    public List<ChatRoomItem> searchChatRoom(Long userId, String name) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUserIdAndTitleLike(userId, name);

        return chatRooms.stream()
                .map(ChatRoomItem::new)
                .toList();
    }

    @Transactional
    public void evictUser(Long userId, Long targetId, Long roomId) {
        ChatUser owner = chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow();

        if (owner.getUserRole().equals(ADMIN)) {
            ChatUser participant = chatUserRepository.findByUserIdAndChatRoomId(targetId, roomId).orElseThrow(
                    () -> new ChatException(NOT_FOUND_CHAT_USER_INFO)
            );
            chatUserRepository.delete(participant);
        } else {
            throw new ChatException(NO_PRINCIPAL);
        }
    }

    @Transactional(readOnly = true)
    public ChatUserInfoResponse getRoomUsers(Long roomId, Long userId) {
        List<ChatUser> chatUsers = chatUserRepository.findByChatRoomId(roomId);

        ChatUser chatUser = chatUsers.stream()
                .filter(cu -> cu.getUser().getId().equals(userId))
                .findAny()
                .orElseThrow(() -> new ChatException(NO_PRINCIPAL));

        chatUsers.removeIf(a -> a.getUser().getId().equals(userId));

        return new ChatUserInfoResponse(chatUser, chatUsers);
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createChatRoom(CreateRoomEvent createRoomEvent) {
        Party party = partyRepository.findById(createRoomEvent.getPartyId()).orElseThrow(
                () -> new PartyException(NOT_FOUND_PARTY)
        );
        User user = userRepository.findById(createRoomEvent.getUserId()).orElseThrow(
                () -> new UserException(NOT_FOUND_USER)
        );
        ChatRoom room = createRoom(party, user, GROUP, party.getPartyTitle());
        chatRoomRepository.save(room);

        ChatUser chatUser = ChatUser.createChatUser(room, user, ADMIN, GROUP);
        chatUserRepository.save(chatUser);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    @Transactional
    public void sendMessage(Long userId, ChatMessage chatMessage) {
        Long roomId = chatMessage.getRoomId();
        chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(
                () -> new ChatException(NO_PRINCIPAL)
        );

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }
}
