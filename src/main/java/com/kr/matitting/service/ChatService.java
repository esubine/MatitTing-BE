package com.kr.matitting.service;

import com.kr.matitting.dto.ChatMessage;
import com.kr.matitting.dto.ResponseChatDto;
import com.kr.matitting.dto.ResponseChatRoomListDto;
import com.kr.matitting.dto.ResponseChatRoomUserDto;
import com.kr.matitting.entity.ChatRoom;
import com.kr.matitting.entity.ChatUser;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.chat.ChatException;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kr.matitting.constant.Role.HOST;
import static com.kr.matitting.constant.Role.VOLUNTEER;
import static com.kr.matitting.dto.ChatRoomDto.CreateRoomEvent;
import static com.kr.matitting.dto.ChatRoomDto.JoinRoomEvent;
import static com.kr.matitting.entity.ChatRoom.createRoom;
import static com.kr.matitting.exception.chat.ChatExceptionType.*;
import static com.kr.matitting.exception.party.PartyExceptionType.NOT_FOUND_PARTY;
import static com.kr.matitting.exception.user.UserExceptionType.INVALID_ROLE_USER;
import static com.kr.matitting.exception.user.UserExceptionType.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomRepositoryImpl chatRoomRepositoryImpl;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    // 내 전체 채팅방 조회
    @Transactional(readOnly = true)
    public ResponseChatRoomListDto getChatRooms(Long userId, Long lastId, Pageable pageable) {
        if (chatUserRepository.findByUserId(userId).isEmpty()) {
            throw new ChatException(IS_NOT_HAVE_CHAT_ROOM);
        }

        LocalDateTime time = (lastId == 0L) ? null : getModifiedDate(lastId);
        return chatRoomRepositoryImpl.getChatRooms(userId, time, pageable);
    }

    private LocalDateTime getModifiedDate(Long lastId) {
        Optional<ChatRoom> chatRoom = chatRoomRepository.findById(lastId);
        if (chatRoom.isPresent()) {
            return chatRoom.get().getModifiedDate();
        } else {
            throw new ChatException(NOT_FOUND_CHAT_ROOM);
        }
    }

    @Transactional(readOnly = true)
    public List<ResponseChatDto> getChats(Long userId, Long roomId, Long lastChatId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));

        return chatRoom.getChatList().stream()
                .map(chat -> {
                    ChatUser user = chat.getSendUser();
                    return ResponseChatDto.builder()
                            .senderId(user.getId())
                            .nickname(user.getNickname())
                            .message(chat.getMessage())
                            .createAt(chat.getCreateDate())
                            .build();
                }).toList();
    }

    // 채팅방 유저 강퇴 - 방장만 가능
    @Transactional
    public void evictUser(Long userId, Long targetId, Long roomId) {
        ChatUser owner = chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow();

        if (owner.getUserRole().equals(HOST)) {
            ChatUser participant = chatUserRepository.findByUserIdAndChatRoomId(targetId, roomId).orElseThrow(
                    () -> new ChatException(NOT_FOUND_CHAT_USER_INFO)
            );
            chatUserRepository.delete(participant);
        } else {
            throw new ChatException(NO_PRINCIPAL);
        }
    }

    // 채팅방 내 유저들의 정보 조회
    @Transactional(readOnly = true)
    public List<ResponseChatRoomUserDto> getRoomUsers(Long roomId, Long userId) {
        chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));

        chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(() -> new ChatException(NOT_FOUND_USER));

        List<ChatUser> chatUsers = chatUserRepository.findByChatRoomId(roomId);

        return chatUsers.stream()
                .map(ResponseChatRoomUserDto::new)
                .toList();
    }

    //파티방 생성 - 파티 글 생성 완료 시 실행
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void createChatRoom(CreateRoomEvent createRoomEvent) {
        Party party = partyRepository.findById(createRoomEvent.getPartyId()).orElseThrow(
                () -> new PartyException(NOT_FOUND_PARTY)
        );
        User user = userRepository.findById(createRoomEvent.getUserId()).orElseThrow(
                () -> new UserException(NOT_FOUND_USER)
        );
        ChatRoom room = createRoom(party, user, party.getPartyTitle());
        chatRoomRepository.save(room);

        ChatUser chatUser = ChatUser.createChatUser(room, user, HOST);
        chatUserRepository.save(chatUser);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    @Transactional
    public void sendMessage(Long userId, ChatMessage chatMessage) {
        Long roomId = chatMessage.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        chatRoom.setModifiedDate(LocalDateTime.now());
        chatRoom.getChatUserList().stream().map(user -> user.getUser().getId().equals(userId)).findAny().orElseThrow(() -> new UserException(INVALID_ROLE_USER));

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
    }

    // 파티 참가 요청 수락 시 채팅유저에 정보 추가
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void addParticipant(JoinRoomEvent joinRoomEvent) {

        ChatRoom chatRoom = chatRoomRepository.findByPartyId(joinRoomEvent.getPartyId()).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));

        User user = userRepository.findById(joinRoomEvent.getUserId()).orElseThrow(() -> new UserException(NOT_FOUND_USER));

        ChatUser chatUser = ChatUser.createChatUser(chatRoom, user, VOLUNTEER);

        chatUserRepository.save(chatUser);
    }
}
