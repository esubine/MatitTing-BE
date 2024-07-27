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

import static com.kr.matitting.constant.Role.HOST;
import static com.kr.matitting.constant.Role.VOLUNTEER;
import static com.kr.matitting.exception.chat.ChatExceptionType.*;

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
    private final EntityFacade entityFacade;

    // 내 전체 채팅방 조회
    @Transactional(readOnly = true)
    public ResponseChatRoomListDto getChatRooms(Long userId, Pageable pageable) {
        entityFacade.getChatUsersByUserId(userId);
        return chatRoomRepositoryImpl.getChatRooms(userId, pageable);
    }

    @Transactional(readOnly = true)
    public ResponseChatRoomListDto getChatRoomsByTitleSearch(Long userId, Pageable pageable, String searchTitle) {
        entityFacade.getChatUsersByUserId(userId);
        return chatRoomRepositoryImpl.getChatRoomsByTitleSearch(userId, pageable, searchTitle);
    }

    @Transactional(readOnly = true)
    public ResponseChatListDto getChats(Long userId, Long roomId, Pageable pageable) {
        entityFacade.getChatRoom(roomId);
        entityFacade.getChatUserByUserIdAndChatRoomId(userId, roomId);

        return chatRepositoryCustomImpl.getChatList(roomId, pageable);
    }

    // 채팅방 유저 강퇴 - 방장만 가능
    @Transactional
    public void evictUser(Long userId, Long targetChatUserId, Long roomId) {
        ChatUser owner = entityFacade.getChatUserByUserIdAndChatRoomId(userId, roomId);
        if (owner.getUserRole().equals(HOST)) {
            ChatUser participant = entityFacade.getChatUserByChatUserId(targetChatUserId);
            String exitMessage = participant.getNickname() + "님이 퇴장했습니다.";
            ChatRoom chatRoom = entityFacade.getChatRoom(roomId);

            Chat exitChat = new Chat(participant, chatRoom, exitMessage, MessageType.EXIT);
            chatRepository.save(exitChat);

            participant.setDeleted(true);

            ChatMessageDto chatMessageDto = new ChatMessageDto();
            chatMessageDto.setRoomId(roomId);
            chatMessageDto.setChatUserId(participant.getId());
            chatMessageDto.setMessage(exitMessage);
            chatMessageDto.setType(MessageType.EXIT);

            messagingTemplate.convertAndSend("/sub/chat/room/" + roomId, chatMessageDto);
        } else {
            throw new ChatException(NO_PRINCIPAL);
        }
    }

    // 채팅방 내 유저들의 정보 조회
    @Transactional(readOnly = true)
    public ResponseChatUserList getRoomUsers(Long roomId, Long userId) {
        entityFacade.getChatRoom(roomId);
        ChatUser requestChatUser = entityFacade.getChatUserByUserIdAndChatRoomId(userId, roomId);

//        List<ChatUser> chatUsers = entityFacade.getChatUsersByRoomId(roomId);
        List<ChatUser> chatUsers = chatRoomRepositoryImpl.getChatUsers(roomId);
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
        ChatRoom chatRoom = entityFacade.getChatRoom(roomId);

        ChatUser sendUser = entityFacade.getChatUser(chatMessageDto.getChatUserId());
        chatRoom.setModifiedDate(LocalDateTime.now());

        chatRoom.getChatUserList().stream()
                .filter(chatUser -> chatUser.equals(sendUser))
                .findAny()
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT_USER_INFO));

        String message = chatMessageDto.getMessage();
        MessageType messageType = chatMessageDto.getType();

        if (MessageType.ENTER.equals(messageType)) {
            message = sendUser.getNickname() + "님이 입장하였습니다.";
            chatMessageDto.setMessage(message);
        }

        Chat chat = new Chat(sendUser, chatRoom, message, messageType);
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

        Chat inviteChat = new Chat(chatUser, chatRoom, message, MessageType.ENTER);
        chatRepository.save(inviteChat);

        ChatMessageDto chatMessageDto = new ChatMessageDto();
        chatMessageDto.setRoomId(chatRoom.getId());
        chatMessageDto.setChatUserId(volunteer.getId());
        chatMessageDto.setMessage(message);
        chatMessageDto.setType(MessageType.ENTER);

        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getId(), chatMessageDto);

        chatUserRepository.save(chatUser);
    }

    public ResponseChatRoomInfoDto getChatRoomInfo(Long chatRoomId, Long userId) {
        // 채팅방 정보
        ChatRoom chatRoom = entityFacade.getChatRoom(chatRoomId);
        ChatRoomInfoRes chatRoomInfoRes = new ChatRoomInfoRes(chatRoom);
        //유저 정보
        ResponseChatUserList responseChatUserList = getRoomUsers(chatRoomId, userId);

        return new ResponseChatRoomInfoDto(chatRoomInfoRes, responseChatUserList);
    }
}
