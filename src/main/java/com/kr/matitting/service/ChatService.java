package com.kr.matitting.service;

import com.kr.matitting.constant.MessageType;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.*;
import com.kr.matitting.exception.chat.ChatException;
import com.kr.matitting.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kr.matitting.constant.Role.HOST;
import static com.kr.matitting.constant.Role.VOLUNTEER;
import static com.kr.matitting.exception.chat.ChatExceptionType.*;
import static com.kr.matitting.exception.user.UserExceptionType.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepositoryImpl chatRoomRepositoryImpl;
    private final ChatRepositoryCustomImpl chatRepositoryCustomImpl;
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
    public ResponseChatListDto getChats(Long userId, Long roomId, Long lastChatId, Pageable pageable) {
        chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));

        return chatRepositoryCustomImpl.getChatList(roomId, pageable, lastChatId);
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
    @Transactional
    public ChatRoom createChatRoom(Party party, User user) {
        ChatRoom room = new ChatRoom(party, user, party.getPartyTitle());
        chatRoomRepository.save(room);

        ChatUser chatUser = new ChatUser(room, user, HOST);
        chatUserRepository.save(chatUser);

        return room;

    }

    @Transactional(readOnly = true)
    public List<ChatRoom> findAll() {
        return chatRoomRepository.findAll();
    }

    @Transactional
    public void sendMessage(User user, ChatMessageDto chatMessageDto) {
        Long roomId = chatMessageDto.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        ChatUser sendUser = chatUserRepository.findByUserIdAndChatRoomId(user.getId(), chatRoom.getId()).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));
        chatRoom.setModifiedDate(LocalDateTime.now());

        chatRoom.getChatUserList().stream()
                .filter(chatUser -> chatUser.getUser().equals(user))
                .findAny()
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));

        if (MessageType.ENTER.equals(chatMessageDto.getType())) {
            chatMessageDto.setMessage(chatMessageDto.getChatUserId() + "님이 입장하였습니다.");
        } else {
            Chat chat = new Chat(sendUser, chatRoom, chatMessageDto.getMessage());
            chatRepository.save(chat);
        }

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);
    }

    // 파티 참가 요청 수락 시 채팅유저에 정보 추가
    @Transactional
    public void addParticipant(Party party, User volunteer) {

        ChatRoom chatRoom = chatRoomRepository.findByPartyId(party.getId())
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        ChatUser chatUser = new ChatUser(chatRoom, volunteer, VOLUNTEER);

        chatUserRepository.save(chatUser);
    }

    public ResponseChatRoomInfoDto getChatRoomInfo(Long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));

        return new ResponseChatRoomInfoDto(chatRoom);
    }
}
