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
    public ResponseChatRoomListDto getChatRooms(Long userId, Pageable pageable) {
        if (chatUserRepository.findByUserId(userId).isEmpty()) {
            throw new ChatException(IS_NOT_HAVE_CHAT_ROOM);
        }
        return chatRoomRepositoryImpl.getChatRooms(userId, pageable);
    }

    @Transactional(readOnly = true)
    public ResponseChatRoomListDto getChatRoomsByTitleSearch(Long userId, Pageable pageable, String searchTitle) {
        if (chatUserRepository.findByUserId(userId).isEmpty()) {
            throw new ChatException(IS_NOT_HAVE_CHAT_ROOM);
        }
        return chatRoomRepositoryImpl.getChatRoomsByTitleSearch(userId, pageable, searchTitle);
    }

    @Transactional(readOnly = true)
    public ResponseChatListDto getChats(Long userId, Long roomId, Pageable pageable) {
        chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));

        return chatRepositoryCustomImpl.getChatList(roomId, pageable);
    }

    // 채팅방 유저 강퇴 - 방장만 가능
    @Transactional
    public void evictUser(Long userId, Long targetChatUserId, Long roomId) {
        ChatUser owner = chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow();

        if (owner.getUserRole().equals(HOST)) {
            ChatUser participant = chatUserRepository.findByIdAndChatRoomId(targetChatUserId, roomId).orElseThrow(
                    () -> new ChatException(NOT_FOUND_CHAT_USER_INFO)
            );
            String exitMessage = participant.getNickname() + "님이 퇴장했습니다.";
            ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
            Chat exitChat = new Chat(participant, chatRoom, exitMessage);
            chatRepository.save(exitChat);

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, exitMessage);
        } else {
            throw new ChatException(NO_PRINCIPAL);
        }
    }

    // 채팅방 내 유저들의 정보 조회
    @Transactional(readOnly = true)
    public ResponseChatUserList getRoomUsers(Long roomId, Long userId) {
        chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));

        ChatUser requestChatUser = chatUserRepository.findByUserIdAndChatRoomId(userId, roomId).orElseThrow(() -> new ChatException(NOT_FOUND_USER));

        List<ChatUser> chatUsers = chatUserRepository.findByChatRoomId(roomId);

        List<ResponseChatRoomUserDto> responseChatRoomUserDtos = chatUsers.stream()
                .map(ResponseChatRoomUserDto::new)
                .toList();

        ResponseMyChatUserInfo responseMyChatUserInfo = new ResponseMyChatUserInfo(requestChatUser);

        return new ResponseChatUserList(responseChatRoomUserDtos, responseMyChatUserInfo);
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
    public void sendMessage(ChatMessageDto chatMessageDto) {
        Long roomId = chatMessageDto.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        ChatUser sendUser = chatUserRepository.findById(chatMessageDto.getChatUserId()).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));
        chatRoom.setModifiedDate(LocalDateTime.now());

        chatRoom.getChatUserList().stream()
                .filter(chatUser -> chatUser.equals(sendUser))
                .findAny()
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));

        String message = chatMessageDto.getMessage();
        if (MessageType.ENTER.equals(chatMessageDto.getType())) {
            message = sendUser.getNickname() + "님이 입장하였습니다.";
            chatMessageDto.setMessage(message);
        }

        Chat chat = new Chat(sendUser, chatRoom, message);
        chatRepository.save(chat);

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessageDto.getRoomId(), chatMessageDto);
    }

    // 파티 참가 요청 수락 시 채팅유저에 정보 추가
    @Transactional
    public void addParticipant(Party party, User volunteer) {

        ChatRoom chatRoom = chatRoomRepository.findByPartyId(party.getId())
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        ChatUser chatUser = new ChatUser(chatRoom, volunteer, VOLUNTEER);

        String message = volunteer.getNickname() + "님이 초대되었습니다.";

        Chat inviteChat = new Chat(chatUser, chatRoom, message);
        chatRepository.save(inviteChat);

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), message);

        chatUserRepository.save(chatUser);
    }

    public ResponseChatRoomInfoDto getChatRoomInfo(Long chatRoomId, Long userId) {
        // 채팅방 정보
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_ROOM));
        ChatRoomInfoRes chatRoomInfoRes = new ChatRoomInfoRes(chatRoom);
        //유저 정보
        ResponseChatUserList responseChatUserList = getRoomUsers(chatRoomId, userId);

        return new ResponseChatRoomInfoDto(chatRoomInfoRes, responseChatUserList);
    }
}
