package com.kr.matitting.config;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.ChatRoomDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.service.ChatService;
import com.kr.matitting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static com.kr.matitting.dto.ChatRoomDto.*;

@Component
@RequiredArgsConstructor
public class IntialDataTemp implements ApplicationRunner {
    private final ChatService chatService;
    private final UserService userService;
    private final PartyRepository partyRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        User user = userService.signUp(
//                new UserSignUpDto(
//                        createUUID(),
//                        SocialType.NAVER,
//                        createUUID(),
//                        createUUID(),
//                        15,
//                        createUUID(),
//                        Gender.ALL)
//        );

//        Party party = Party.builder()
//                .partyTitle("맛있팅 참여하세요")
//                .partyContent("저는 돈까스를 좋아합니다")
//                .address("서울 마포구 포은로2나길 44")
//                .latitude(37.550457)
//                .longitude(126.909708)
//                .status(PartyStatus.RECRUIT)
//                .deadline(LocalDateTime.now().plusDays(1))
//                .partyTime(LocalDateTime.now().plusDays(3))
//                .totalParticipant(4)
//                .participantCount(1)
//                .gender(Gender.ALL)
//                .age(PartyAge.TWENTY)
//                .hit(0)
//                .menu("치~즈돈까스")
//                .category(PartyCategory.JAPANESE)
//                .user(user)
//                .build();
//
//        partyRepository.save(party);
//
//        chatService.createChatRoom(new CreateRoomEvent(party.getId(), user.getId()));
//        chatService.createChatRoom(new CreateGroupRoomEvent());
//        chatService.createRoomEvent(new CreateRoomEvent(null, null));
//        chatService.createRoomEvent(new CreateRoomEvent(null, null));
    }

    private String createUUID() {
        return UUID.randomUUID().toString().substring(0, 10);
    }
}
