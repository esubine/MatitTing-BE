package com.kr.matitting;

import com.kr.matitting.constant.*;
import com.kr.matitting.entity.*;
import com.kr.matitting.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static com.kr.matitting.constant.Role.HOST;
import static com.kr.matitting.constant.Role.VOLUNTEER;

@RequiredArgsConstructor
@Component
public class ProductInitializer implements ApplicationRunner {
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final PartyJoinRepository partyJoinRepository;
    private final PartyTeamRepository teamRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<User> userList = createUser();
        List<Party> partyList = createParty(userList);
        List<Team> teamList = createTeam(partyList, userList);
        List<ChatRoom> chatRoomList = createChatRoom(partyList, userList);
        List<ChatUser> chatUserList = createChatUser(chatRoomList, userList);
    }

    private List<User> createUser() {
        User user1 = User.builder()
                .socialId("323123123")
                .oauthProvider(OauthProvider.KAKAO)
                .email("one@kakao.com")
                .nickname("첫째")
                .age(26)
                .imgUrl("고양이사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .socialId("18515513")
                .oauthProvider(OauthProvider.NAVER)
                .email("two@naver.com")
                .nickname("둘째")
                .age(32)
                .imgUrl("강아지사진.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user3 = User.builder()
                .socialId("121311513")
                .oauthProvider(OauthProvider.KAKAO)
                .email("three@kakao.com")
                .nickname("셋째")
                .age(30)
                .imgUrl("악어.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user4 = User.builder()
                .socialId("998877")
                .oauthProvider(OauthProvider.NAVER)
                .email("four@naver.com")
                .nickname("넷째")
                .age(42)
                .imgUrl("다람쥐.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user5 = User.builder()
                .socialId("77548465")
                .oauthProvider(OauthProvider.KAKAO)
                .email("five@kakao.com")
                .nickname("다섯째")
                .age(52)
                .imgUrl("하마.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user6 = User.builder()
                .socialId("41568531")
                .oauthProvider(OauthProvider.NAVER)
                .email("six@naver.com")
                .nickname("여섯째")
                .age(52)
                .imgUrl("돼지.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user7 = User.builder()
                .socialId("44235481")
                .oauthProvider(OauthProvider.KAKAO)
                .email("seven@kakao.com")
                .nickname("일곱째")
                .age(22)
                .imgUrl("사자.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user8 = User.builder()
                .socialId("131365656")
                .oauthProvider(OauthProvider.NAVER)
                .email("eight@naver.com")
                .nickname("여덞째")
                .age(27)
                .imgUrl("강아지사진.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user9 = User.builder()
                .socialId("5151655616")
                .oauthProvider(OauthProvider.KAKAO)
                .email("nine@kakao.com")
                .nickname("아홉째")
                .age(32)
                .imgUrl("표범.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user10 = User.builder()
                .socialId("0054051")
                .oauthProvider(OauthProvider.NAVER)
                .email("ten@naver.com")
                .nickname("열째")
                .age(38)
                .imgUrl("코끼리.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();


        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);
        userRepository.save(user5);
        userRepository.save(user6);
        userRepository.save(user7);
        userRepository.save(user8);
        userRepository.save(user9);
        userRepository.save(user10);

        List<User> userList = userRepository.findAll();
        return userList;
    }

    private List<Party> createParty(List<User> userList) {
        Party party1 = Party.builder()
                .partyTitle("로우키입니다.")
                .partyContent("성수 커피 맛집입니다~")
                .partyPlaceName("로우키")
                .address("서울 성동구 연무장3길 6")
                .latitude(37.54419081960767)
                .longitude(127.0515738292837)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3).minusHours(1))
                .partyTime(LocalDateTime.now().plusDays(3))
                .totalParticipant(4)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.TWENTY)
                .hit(0)
                .menu("카페")
                .category(PartyCategory.ETC)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/etc.jpeg")
                .user(userList.get(0))
                .build();


        Party party2 = Party.builder()
                .partyTitle("소문난 성수 감자탕 같이 먹어요!")
                .partyContent("뜨끈한 감자탕에 볶음밥까지~~")
                .partyPlaceName("소문난 성수 감자탕")
                .address("서울 성동구 연무장길 45")
                .latitude(37.54283579127469)
                .longitude(127.05441872600115)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3).minusHours(5))
                .partyTime(LocalDateTime.now().plusDays(3).minusHours(4))
                .totalParticipant(5)
                .participantCount(1)
                .gender(Gender.MALE)
                .age(PartyAge.THIRTY)
                .hit(0)
                .menu("감자탕")
                .category(PartyCategory.KOREAN)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
                .user(userList.get(1))
                .build();

        Party party3 = Party.builder()
                .partyTitle("우동 드실 분!")
                .partyContent("우동 한 그릇 어떠신가요?")
                .partyPlaceName("우동카덴")
                .address("서울 마포구 양화로7안길 2-1")
                .latitude(37.55168135609219)
                .longitude(126.91506060313031)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(7).minusHours(9))
                .partyTime(LocalDateTime.now().plusDays(7).minusHours(8))
                .totalParticipant(3)
                .participantCount(1)
                .gender(Gender.FEMALE)
                .age(PartyAge.FORTY)
                .hit(0)
                .menu("우동카덴")
                .category(PartyCategory.JAPANESE)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .user(userList.get(2))
                .build();

        Party party4 = Party.builder()
                .partyTitle("푸라닭 치킨")
                .partyContent("순살 vs 뼈")
                .partyPlaceName("푸라닭 치킨")
                .address("서울 마포구 양화로 72")
                .latitude(37.55106321505722)
                .longitude(126.91688045929288)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(2).minusHours(1))
                .partyTime(LocalDateTime.now().plusDays(2))
                .totalParticipant(6)
                .participantCount(1)
                .gender(Gender.MALE)
                .age(PartyAge.TWENTY)
                .hit(0)
                .menu("치킨")
                .category(PartyCategory.KOREAN)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
                .user(userList.get(3))
                .build();

        Party party5 = Party.builder()
                .partyTitle("멘야마쯔리 라멘")
                .partyContent("라멘 같이 드실 분~~")
                .partyPlaceName("멘야마쯔리 라멘")
                .address("서서울 마포구 와우산로 112")
                .latitude(37.55313022639803)
                .longitude(126.92585818951)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(7).minusHours(10))
                .partyTime(LocalDateTime.now().plusDays(7).minusHours(9))
                .totalParticipant(3)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.ALL)
                .hit(0)
                .menu("라멘")
                .category(PartyCategory.JAPANESE)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .user(userList.get(4))
                .build();

        Party party6 = Party.builder()
                .partyTitle("마라탕")
                .partyContent("마라탕 같이 드실 분~~")
                .partyPlaceName("마라탕집")
                .address("서울 마포구 와우산로23길 50")
                .latitude(37.55502996957031)
                .longitude(126.92372017268688)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(2).minusHours(3))
                .partyTime(LocalDateTime.now().plusDays(2).minusHours(2))
                .totalParticipant(5)
                .participantCount(1)
                .gender(Gender.FEMALE)
                .age(PartyAge.THIRTY)
                .hit(0)
                .menu("마라탕")
                .category(PartyCategory.CHINESE)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/chinese.jpeg")
                .user(userList.get(5))
                .build();

        Party party7 = Party.builder()
                .partyTitle("배스킨라빈스")
                .partyContent("민초파 환영합니다.")
                .partyPlaceName("베스킨라빈스")
                .address("서울 중구 서소문로 141")
                .latitude(37.564312240408036)
                .longitude(126.97636518813464)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3).minusHours(1))
                .partyTime(LocalDateTime.now().plusDays(3))
                .totalParticipant(3)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.ALL)
                .hit(0)
                .menu("배스킨라빈스")
                .category(PartyCategory.ETC)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/etc.jpeg")
                .user(userList.get(6))
                .build();

        Party party8 = Party.builder()
                .partyTitle("제스티살룬")
                .partyContent("버거 같이 먹어요!")
                .partyPlaceName("제스티살룬")
                .address("서울 성동구 서울숲4길 13")
                .latitude(37.54756419158108)
                .longitude(127.04244119692027)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(4).minusHours(4))
                .partyTime(LocalDateTime.now().plusDays(4).minusHours(3))
                .totalParticipant(6)
                .participantCount(1)
                .gender(Gender.FEMALE)
                .age(PartyAge.ALL)
                .hit(0)
                .menu("버거")
                .category(PartyCategory.WESTERN)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/western.jpeg")
                .user(userList.get(7))
                .build();


        Party party9 = Party.builder()
                .partyTitle("벱")
                .partyContent("쌀국수 같이 먹어요!")
                .partyPlaceName("벱")
                .address("서울 성동구 성수일로4가길 2")
                .latitude(37.54206337663051)
                .longitude(127.05399949807126)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3).minusHours(1))
                .partyTime(LocalDateTime.now().plusDays(3))
                .totalParticipant(4)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.FORTY)
                .hit(0)
                .menu("베트남 음식")
                .category(PartyCategory.ETC)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/etc.jpeg")
                .user(userList.get(8))
                .build();

        Party party10 = Party.builder()
                .partyTitle("등갈비")
                .partyContent("등갈비 같이 먹어요!")
                .partyPlaceName("등갈비의 달인")
                .address("서울 강동구 천호대로158길 25")
                .latitude(37.53629127200383)
                .longitude(127.1263624712968)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(8).minusHours(8))
                .partyTime(LocalDateTime.now().plusDays(8).minusHours(7))
                .totalParticipant(4)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.FORTY)
                .hit(0)
                .menu("등갈비")
                .category(PartyCategory.KOREAN)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/korean.jpeg")
                .user(userList.get(9))
                .build();

        partyRepository.save(party1);
        partyRepository.save(party2);
        partyRepository.save(party3);
        partyRepository.save(party4);
        partyRepository.save(party5);
        partyRepository.save(party6);
        partyRepository.save(party7);
        partyRepository.save(party8);
        partyRepository.save(party9);
        partyRepository.save(party10);

        List<Party> partyList = partyRepository.findAll();
        return partyList;
    }

    private List<Team> createTeam(List<Party> partyList, List<User> userList) {
        Random random = new Random(); // 랜덤 객체 생성
        random.setSeed(System.currentTimeMillis());

        Team team1 = Team.builder()
                .party(partyList.get(0))
                .user(partyList.get(0).getUser())
                .role(Role.HOST)
                .build();
        Team team1_1 = Team.builder()
                .party(partyList.get(0))
                .user(userList.get(1))
                .role(Role.VOLUNTEER)
                .build();
        Team team1_2 = Team.builder()
                .party(partyList.get(0))
                .user(userList.get(2))
                .role(Role.VOLUNTEER)
                .build();
        Team team1_3 = Team.builder()
                .party(partyList.get(0))
                .user(userList.get(3))
                .role(Role.VOLUNTEER)
                .build();

        Team team2 = Team.builder()
                .party(partyList.get(1))
                .user(partyList.get(1).getUser())
                .role(Role.HOST)
                .build();
        Team team2_1 = Team.builder()
                .party(partyList.get(1))
                .user(userList.get(2))
                .role(Role.VOLUNTEER)
                .build();
        Team team2_2 = Team.builder()
                .party(partyList.get(1))
                .user(userList.get(3))
                .role(Role.VOLUNTEER)
                .build();
        Team team2_3 = Team.builder()
                .party(partyList.get(1))
                .user(userList.get(4))
                .role(Role.VOLUNTEER)
                .build();

        Team team3 = Team.builder()
                .party(partyList.get(2))
                .user(partyList.get(2).getUser())
                .role(Role.HOST)
                .build();
        Team team3_1 = Team.builder()
                .party(partyList.get(2))
                .user(userList.get(3))
                .role(Role.VOLUNTEER)
                .build();
        Team team3_2 = Team.builder()
                .party(partyList.get(2))
                .user(userList.get(4))
                .role(Role.VOLUNTEER)
                .build();
        Team team3_3 = Team.builder()
                .party(partyList.get(2))
                .user(userList.get(5))
                .role(Role.VOLUNTEER)
                .build();

        Team team4 = Team.builder()
                .party(partyList.get(3))
                .user(partyList.get(3).getUser())
                .role(Role.HOST)
                .build();
        Team team4_1 = Team.builder()
                .party(partyList.get(3))
                .user(userList.get(4))
                .role(Role.VOLUNTEER)
                .build();
        Team team4_2 = Team.builder()
                .party(partyList.get(3))
                .user(userList.get(5))
                .role(Role.VOLUNTEER)
                .build();
        Team team4_3 = Team.builder()
                .party(partyList.get(3))
                .user(userList.get(6))
                .role(Role.VOLUNTEER)
                .build();

        teamRepository.save(team1);
        teamRepository.save(team1_1);
        teamRepository.save(team1_2);
        teamRepository.save(team1_3);
        teamRepository.save(team2);
        teamRepository.save(team2_1);
        teamRepository.save(team2_2);
        teamRepository.save(team2_3);
        teamRepository.save(team3);
        teamRepository.save(team3_1);
        teamRepository.save(team3_2);
        teamRepository.save(team3_3);
        teamRepository.save(team4);
        teamRepository.save(team4_1);
        teamRepository.save(team4_2);
        teamRepository.save(team4_3);

        return teamRepository.findAll();
    }

    private List<ChatRoom> createChatRoom(List<Party> partyList, List<User> userList) {
        ChatRoom room1 = new ChatRoom(partyList.get(0), userList.get(0), partyList.get(0).getPartyTitle());
        ChatRoom room2 = new ChatRoom(partyList.get(1), userList.get(1), partyList.get(1).getPartyTitle());
        ChatRoom room3 = new ChatRoom(partyList.get(2), userList.get(2), partyList.get(2).getPartyTitle());
        ChatRoom room4 = new ChatRoom(partyList.get(3), userList.get(3), partyList.get(3).getPartyTitle());
        ChatRoom room5 = new ChatRoom(partyList.get(4), userList.get(4), partyList.get(4).getPartyTitle());
        ChatRoom room6 = new ChatRoom(partyList.get(5), userList.get(5), partyList.get(5).getPartyTitle());
        ChatRoom room7 = new ChatRoom(partyList.get(6), userList.get(6), partyList.get(6).getPartyTitle());
        ChatRoom room8 = new ChatRoom(partyList.get(7), userList.get(7), partyList.get(7).getPartyTitle());
        ChatRoom room9 = new ChatRoom(partyList.get(8), userList.get(8), partyList.get(8).getPartyTitle());
        ChatRoom room10 = new ChatRoom(partyList.get(9), userList.get(9), partyList.get(9).getPartyTitle());

        chatRoomRepository.save(room1);
        chatRoomRepository.save(room2);
        chatRoomRepository.save(room3);
        chatRoomRepository.save(room4);
        chatRoomRepository.save(room5);
        chatRoomRepository.save(room6);
        chatRoomRepository.save(room7);
        chatRoomRepository.save(room8);
        chatRoomRepository.save(room9);
        chatRoomRepository.save(room10);

        return chatRoomRepository.findAll();
    }

    private List<ChatUser> createChatUser(List<ChatRoom> chatRoomList, List<User> userList){

        ChatUser chatUser1 = new ChatUser(chatRoomList.get(0), userList.get(0), HOST);
        ChatUser chatUser1_1 = new ChatUser(chatRoomList.get(0), userList.get(1), VOLUNTEER);
        ChatUser chatUser1_2 = new ChatUser(chatRoomList.get(0), userList.get(2), VOLUNTEER);
        ChatUser chatUser1_3 = new ChatUser(chatRoomList.get(0), userList.get(3), VOLUNTEER);

        ChatUser chatUser2 = new ChatUser(chatRoomList.get(1), userList.get(1), HOST);
        ChatUser chatUser2_1 = new ChatUser(chatRoomList.get(1), userList.get(2), VOLUNTEER);
        ChatUser chatUser2_2 = new ChatUser(chatRoomList.get(1), userList.get(3), VOLUNTEER);
        ChatUser chatUser2_3 = new ChatUser(chatRoomList.get(1), userList.get(4), VOLUNTEER);

        ChatUser chatUser3 = new ChatUser(chatRoomList.get(2), userList.get(2), HOST);
        ChatUser chatUser3_1 = new ChatUser(chatRoomList.get(2), userList.get(3), VOLUNTEER);
        ChatUser chatUser3_2 = new ChatUser(chatRoomList.get(2), userList.get(4), VOLUNTEER);
        ChatUser chatUser3_3 = new ChatUser(chatRoomList.get(2), userList.get(5), VOLUNTEER);

        ChatUser chatUser4 = new ChatUser(chatRoomList.get(3), userList.get(3), HOST);
        ChatUser chatUser4_1 = new ChatUser(chatRoomList.get(3), userList.get(4), VOLUNTEER);
        ChatUser chatUser4_2 = new ChatUser(chatRoomList.get(3), userList.get(5), VOLUNTEER);
        ChatUser chatUser4_3 = new ChatUser(chatRoomList.get(3), userList.get(6), VOLUNTEER);

        ChatUser chatUser5 = new ChatUser(chatRoomList.get(4), userList.get(4), HOST);
        ChatUser chatUser6 = new ChatUser(chatRoomList.get(5), userList.get(5), HOST);
        ChatUser chatUser7 = new ChatUser(chatRoomList.get(6), userList.get(6), HOST);
        ChatUser chatUser8 = new ChatUser(chatRoomList.get(7), userList.get(7), HOST);
        ChatUser chatUser9 = new ChatUser(chatRoomList.get(8), userList.get(8), HOST);
        ChatUser chatUser10 = new ChatUser(chatRoomList.get(9), userList.get(9), HOST);

        chatUserRepository.save(chatUser1);
        chatUserRepository.save(chatUser1_1);
        chatUserRepository.save(chatUser1_2);
        chatUserRepository.save(chatUser1_3);

        chatUserRepository.save(chatUser2);
        chatUserRepository.save(chatUser2_1);
        chatUserRepository.save(chatUser2_2);
        chatUserRepository.save(chatUser2_3);

        chatUserRepository.save(chatUser3);
        chatUserRepository.save(chatUser3_1);
        chatUserRepository.save(chatUser3_2);
        chatUserRepository.save(chatUser3_3);

        chatUserRepository.save(chatUser4);
        chatUserRepository.save(chatUser4_1);
        chatUserRepository.save(chatUser4_2);
        chatUserRepository.save(chatUser4_3);

        chatUserRepository.save(chatUser5);
        chatUserRepository.save(chatUser6);
        chatUserRepository.save(chatUser7);
        chatUserRepository.save(chatUser8);
        chatUserRepository.save(chatUser9);
        chatUserRepository.save(chatUser10);

        return chatUserRepository.findAll();
    }
}
