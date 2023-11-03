package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.entity.*;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.partyjoin.PartyJoinException;

import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyTeamRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

@Slf4j
@Transactional
@SpringBootTest
class PartyServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private PartyJoinRepository partyJoinRepository;
    @Autowired
    private PartyTeamRepository teamRepository;
    @Autowired
    private PartyService partyService;
    @Autowired
    private MapService mapService;

    public static Long partyId;
    public static List<Long> partyId_list = new ArrayList<>();
    public static String guestSocialId = "12345";

    @BeforeEach
    public void 데이터생성() {
        User user = User.builder()
                .socialId("12345")
                .socialType(SocialType.KAKAO)
                .email("test@naver.com")
                .nickname("새싹개발자")
                .age(26)
                .imgUrl("증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        userRepository.save(user);

        Party party = Party.builder()
                .partyTitle("맛있팅 참여하세요")
                .partyContent("저는 돈까스를 좋아합니다")
                .address("서울 마포구 포은로2나길 44")
                .latitude(37.550457)
                .longitude(126.909708)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(1))
                .partyTime(LocalDateTime.now().plusDays(3))
                .totalParticipant(4)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.AGE2030)
                .hit(0)
                .menu("치~즈돈까스")
                .category(PartyCategory.JAPANESE)
                .user(user)
                .build();

        Party save = partyRepository.save(party);
        partyId = save.getId();

        User user1 = User.builder()
                .socialId("098")
                .socialType(SocialType.KAKAO)
                .email("user1@naver.com")
                .nickname("User1")
                .age(26)
                .imgUrl("첫째 증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .socialId("456")
                .socialType(SocialType.NAVER)
                .email("user2@naver.com")
                .nickname("User2")
                .age(20)
                .imgUrl("둘째 증명사진.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user3 = User.builder()
                .socialId("836")
                .socialType(SocialType.KAKAO)
                .email("user3@naver.com")
                .nickname("User3")
                .age(36)
                .imgUrl("셋째 증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        List<String> title_list = new ArrayList<>();
        title_list.add("서울에서 만나요");
        title_list.add("수원에서 만나요");
        title_list.add("전주에서 만나요");
        title_list.add("광주에서 만나요");
        title_list.add("전국에서 만나요");

        List<PartyStatus> status_list = new ArrayList<>();
        status_list.add(PartyStatus.RECRUIT);
        status_list.add(PartyStatus.RECRUIT);
        status_list.add(PartyStatus.FINISH);
        status_list.add(PartyStatus.RECRUIT);
        status_list.add(PartyStatus.FINISH);
        status_list.add(PartyStatus.FINISH);
        status_list.add(PartyStatus.RECRUIT);

        List<String> menu_list = new ArrayList<>();
        menu_list.add("돈까스");
        menu_list.add("피자");
        menu_list.add("치킨");
        menu_list.add("짜장면");
        menu_list.add("뇨끼");
        menu_list.add("김치볶음밥");
        menu_list.add("삼겹살");

        List<PartyCategory> category_list = new ArrayList<>();
        category_list.add(PartyCategory.JAPANESE);
        category_list.add(PartyCategory.WESTERN);
        category_list.add(PartyCategory.KOREAN);
        category_list.add(PartyCategory.CHINESE);
        category_list.add(PartyCategory.WESTERN);
        category_list.add(PartyCategory.KOREAN);
        category_list.add(PartyCategory.KOREAN);

        List<LocalDateTime> deadline_list = new ArrayList<>();
        deadline_list.add(LocalDateTime.now().plusDays(1));
        deadline_list.add(LocalDateTime.now().plusDays(2));
        deadline_list.add(LocalDateTime.now().plusDays(3));

        List<LocalDateTime> partyTime_list = new ArrayList<>();
        partyTime_list.add(LocalDateTime.now().plusDays(3));
        partyTime_list.add(LocalDateTime.now().plusDays(4));
        partyTime_list.add(LocalDateTime.now().plusDays(5));

        List<Integer> total_list = new ArrayList<>();
        total_list.add(3);
        total_list.add(4);
        total_list.add(5);
        total_list.add(6);

        List<Integer> current_list = new ArrayList<>();
        current_list.add(1);
        current_list.add(2);
        current_list.add(3);

        List<Gender> gender = new ArrayList<>();
        gender.add(Gender.ALL);
        gender.add(Gender.FEMALE);
        gender.add(Gender.MALE);

        List<PartyAge> age = new ArrayList<>();
        age.add(PartyAge.ALL);
        age.add(PartyAge.AGE2030);
        age.add(PartyAge.AGE3040);
        age.add(PartyAge.AGE40);

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            Party newParty = Party.builder()
                    .partyTitle(title_list.get(i % 5))
                    .partyContent(random_word(random, 30))
                    .address(random_word(random, 10))
                    .latitude(Math.random())
                    .longitude(Math.random())
                    .status(status_list.get(i % 7))
                    .deadline(deadline_list.get(i%3))
                    .partyTime(partyTime_list.get(i%3))
                    .totalParticipant(total_list.get(i%4))
                    .participantCount(current_list.get(i%3))
                    .gender(gender.get(i%3))
                    .age(age.get(i%4))
                    .hit((int) Math.random())
                    .menu(menu_list.get(i % 7))
                    .category(category_list.get(i%7))
                    .user(users.get(i%3))
                    .build();
            Party saveParty = partyRepository.save(newParty);
            partyId_list.add(saveParty.getId());
        }
    }

    @AfterEach
    void clean() {
        partyId_list.clear();
    }

    private String random_word(Random random, int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    void 파티_업데이트_성공_모두() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                "맛있팅 참여안하실래요?",
                "저는 피자를 좋아합니다",
                "피자",
                126.909608,
                37.550357,
                PartyStatus.RECRUIT,
                6,
                Gender.FEMALE,
                PartyAge.ALL,
                "피자.jpg",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5));

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여안하실래요?");
        assertThat(party.getPartyContent()).isEqualTo("저는 피자를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("피자");
        assertThat(party.getLongitude()).isEqualTo(126.909608);
        assertThat(party.getLatitude()).isEqualTo(37.550357);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(6);
        assertThat(party.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(party.getAge()).isEqualTo(PartyAge.ALL);
        assertThat(party.getThumbnail()).isEqualTo("피자.jpg");
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(2));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(5));
    }

    @Test
    void 파티_업데이트_성공_제목() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                "맛있팅 참여안하실래요?",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여안하실래요?");
        assertThat(party.getPartyContent()).isEqualTo("저는 돈까스를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("치~즈돈까스");
        assertThat(party.getLongitude()).isEqualTo(126.909708);
        assertThat(party.getLatitude()).isEqualTo(37.550457);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(4);
        assertThat(party.getGender()).isEqualTo(Gender.ALL);
        assertThat(party.getAge()).isEqualTo(PartyAge.AGE2030);
        assertThat(party.getThumbnail()).isNull();
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(1));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(3));
    }

    @Test
    void 파티_업데이트_성공_소개글() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                null,
                "저는 피자를 좋아합니다",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여하세요");
        assertThat(party.getPartyContent()).isEqualTo("저는 피자를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("치~즈돈까스");
        assertThat(party.getLongitude()).isEqualTo(126.909708);
        assertThat(party.getLatitude()).isEqualTo(37.550457);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(4);
        assertThat(party.getGender()).isEqualTo(Gender.ALL);
        assertThat(party.getAge()).isEqualTo(PartyAge.AGE2030);
        assertThat(party.getThumbnail()).isNull();
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(1));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(3));
    }

    @Test
    void 파티_업데이트_성공_메뉴() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                null,
                null,
                "파스타",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여하세요");
        assertThat(party.getPartyContent()).isEqualTo("저는 돈까스를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("파스타");
        assertThat(party.getLongitude()).isEqualTo(126.909708);
        assertThat(party.getLatitude()).isEqualTo(37.550457);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(4);
        assertThat(party.getGender()).isEqualTo(Gender.ALL);
        assertThat(party.getAge()).isEqualTo(PartyAge.AGE2030);
        assertThat(party.getThumbnail()).isNull();
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(1));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(3));
    }

    @Test
    void 파티_업데이트_성공_위경도() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                null,
                null,
                null,
                123.123,
                234.234,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여하세요");
        assertThat(party.getPartyContent()).isEqualTo("저는 돈까스를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("치~즈돈까스");
        assertThat(party.getLongitude()).isEqualTo(123.123);
        assertThat(party.getLatitude()).isEqualTo(234.234);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(4);
        assertThat(party.getGender()).isEqualTo(Gender.ALL);
        assertThat(party.getAge()).isEqualTo(PartyAge.AGE2030);
        assertThat(party.getThumbnail()).isNull();
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(1));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(3));
    }

    @Test
    void 파티_업데이트_성공_마감시간() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                LocalDateTime.now().plusDays(2),
                null);

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여하세요");
        assertThat(party.getPartyContent()).isEqualTo("저는 돈까스를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("치~즈돈까스");
        assertThat(party.getLongitude()).isEqualTo(126.909708);
        assertThat(party.getLatitude()).isEqualTo(37.550457);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(4);
        assertThat(party.getGender()).isEqualTo(Gender.ALL);
        assertThat(party.getAge()).isEqualTo(PartyAge.AGE2030);
        assertThat(party.getThumbnail()).isNull();
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(2));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(3));
    }

    @Test
    void 파티_업데이트_성공_파티시간() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                partyId,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                LocalDateTime.now().plusDays(5));

        //when
        partyService.partyUpdate(partyUpdateDto);
        Party party = partyRepository.findById(partyId).get();

        //then
        assertThat(party.getPartyTitle()).isEqualTo("맛있팅 참여하세요");
        assertThat(party.getPartyContent()).isEqualTo("저는 돈까스를 좋아합니다");
        assertThat(party.getMenu()).isEqualTo("치~즈돈까스");
        assertThat(party.getLongitude()).isEqualTo(126.909708);
        assertThat(party.getLatitude()).isEqualTo(37.550457);
        assertThat(party.getStatus()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(party.getTotalParticipant()).isEqualTo(4);
        assertThat(party.getGender()).isEqualTo(Gender.ALL);
        assertThat(party.getAge()).isEqualTo(PartyAge.AGE2030);
        assertThat(party.getThumbnail()).isNull();
        assertThat(party.getDeadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(1));
        assertThat(party.getPartyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(5));
    }

    @Test
    void 파티_업데이트_실패_ID_없음() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                1000L,
                "맛있팅 참여안하실래요?",
                "저는 피자를 좋아합니다",
                "피자",
                126.909608,
                37.550357,
                PartyStatus.RECRUIT,
                6,
                Gender.FEMALE,
                PartyAge.ALL,
                "피자.jpg",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5));

        //when, then
        assertThrows(PartyException.class, () -> partyService.partyUpdate(partyUpdateDto));
    }

    @Test
    void 파티_업데이트_실패_상태_잘못됨() {
        //given, then
        assertThrows(IllegalArgumentException.class, () -> new PartyUpdateDto(
                partyId,
                "맛있팅 참여안하실래요?",
                "저는 피자를 좋아합니다",
                "피자",
                126.909608,
                37.550357,
                PartyStatus.valueOf("testest"),
                6,
                Gender.FEMALE,
                PartyAge.ALL,
                "피자.jpg",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5)));
    }

    @Test
    void 파티_업데이트_실패_성별_잘못됨() {
        //given, then
        assertThrows(IllegalArgumentException.class, () -> new PartyUpdateDto(
                partyId,
                "맛있팅 참여안하실래요?",
                "저는 피자를 좋아합니다",
                "피자",
                126.909608,
                37.550357,
                PartyStatus.RECRUIT,
                6,
                Gender.valueOf("test"),
                PartyAge.ALL,
                "피자.jpg",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5)));
    }

    @Test
    void 파티_업데이트_실패_연령대_잘못됨() {
        //given, then
        assertThrows(IllegalArgumentException.class, () -> new PartyUpdateDto(
                partyId,
                "맛있팅 참여안하실래요?",
                "저는 피자를 좋아합니다",
                "피자",
                126.909608,
                37.550357,
                PartyStatus.RECRUIT,
                6,
                Gender.MALE,
                PartyAge.valueOf("100세"),
                "피자.jpg",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5)));
    }
    @Test
    void 파티_업데이트_실패_마감시간_잘못됨() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto(
                1000L,
                "맛있팅 참여안하실래요?",
                "저는 피자를 좋아합니다",
                "피자",
                126.909608,
                37.550357,
                PartyStatus.RECRUIT,
                6,
                Gender.ALL,
                PartyAge.ALL,
                "피자.jpg",
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(3));

        //when, then
        assertThrows(PartyException.class, () -> partyService.partyUpdate(partyUpdateDto));
    }

    @Test
    void 파티조회_성공() {
        //when
        ResponsePartyDto partyInfo = partyService.getPartyInfo(partyId);

        //then
        assertThat(partyInfo.partyTitle()).isEqualTo("맛있팅 참여하세요");
        assertThat(partyInfo.partyContent()).isEqualTo("저는 돈까스를 좋아합니다");
        assertThat(partyInfo.menu()).isEqualTo("치~즈돈까스");
        assertThat(partyInfo.longitude()).isEqualTo(126.909708);
        assertThat(partyInfo.latitude()).isEqualTo(37.550457);
        assertThat(partyInfo.status()).isEqualTo(PartyStatus.RECRUIT);
        assertThat(partyInfo.totalParticipate()).isEqualTo(4);
        assertThat(partyInfo.gender()).isEqualTo(Gender.ALL);
        assertThat(partyInfo.age()).isEqualTo(PartyAge.AGE2030);
        assertThat(partyInfo.thumbnail()).isNull();
        assertThat(partyInfo.deadline()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(1));
        assertThat(partyInfo.partyTime()).isEqualToIgnoringMinutes(LocalDateTime.now().plusDays(3));
    }

    @Test
    void 파티조회_실패_ID_없음() {
        //when, then
        assertThrows(PartyException.class, () -> partyService.getPartyInfo(1000L));
    }

    @Test
    void 파티삭제_성공() {
        //when
        partyService.deleteParty(partyId);

        //then
        Optional<Party> party = partyRepository.findById(partyId);
        assertThat(party.isEmpty()).isTrue();
    }

    @Test
    void 파티삭제_실패_ID_없음() {
        //when, then
        assertThrows(PartyException.class, () -> partyService.deleteParty(1000L));
    }

    //TODO: 실패
    @Test
    void 파티신청_성공() {
        //given
        Optional<Party> findParty_01 = partyRepository.findById(partyId_list.get(0));
        User user_01 = findParty_01.get().getUser();
        Optional<Party> findParty_02 = partyRepository.findById(partyId_list.get(1));
        User user_02 = findParty_02.get().getUser();
        Optional<Party> findParty_03 = partyRepository.findById(partyId_list.get(2));
        User user_03 = findParty_03.get().getUser();
        Optional<Party> findParty_04 = partyRepository.findById(partyId_list.get(3));
        User user_04 = findParty_04.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);

        //when
        PartyJoinDto partyJoinDto_01 = new PartyJoinDto(findParty_01.get().getId(), user_01.getId(), guest.get().getId(), PartyJoinStatus.WAIT);
        PartyJoinDto partyJoinDto_02 = new PartyJoinDto(findParty_02.get().getId(), user_02.getId(), guest.get().getId(), PartyJoinStatus.WAIT);
        PartyJoinDto partyJoinDto_03 = new PartyJoinDto(findParty_03.get().getId(), user_03.getId(), guest.get().getId(), PartyJoinStatus.WAIT);
        PartyJoinDto partyJoinDto_04 = new PartyJoinDto(findParty_01.get().getId(), user_04.getId(), guest.get().getId(), PartyJoinStatus.WAIT);
        partyService.joinParty(partyJoinDto_01);
        partyService.joinParty(partyJoinDto_02);
        partyService.joinParty(partyJoinDto_03);
        partyService.joinParty(partyJoinDto_04);

        List<PartyJoin> partyJoinList_01 = partyJoinRepository.findByLeaderId(user_01.getId());
        List<PartyJoin> partyJoinList_02 = partyJoinRepository.findByPartyIdAndLeaderId(findParty_02.get().getId(), user_02.getId());
        List<PartyJoin> partyJoinList_03 = partyJoinRepository.findByPartyIdAndLeaderId(findParty_03.get().getId(), user_03.getId());

        //then
        assertThat(partyJoinList_01.size()).isEqualTo(2);
        assertThat(partyJoinList_02.size()).isEqualTo(1);
        assertThat(partyJoinList_03.size()).isEqualTo(1);

        assertThat(partyJoinList_01.get(0).getLeaderId()).isEqualTo(user_01.getId());
        assertThat(partyJoinList_01.get(1).getLeaderId()).isEqualTo(user_01.getId());
        assertThat(partyJoinList_02.get(0).getLeaderId()).isEqualTo(user_02.getId());
        assertThat(partyJoinList_03.get(0).getLeaderId()).isEqualTo(user_03.getId());
    }

    @Test
    void 파티신청_실패_파티Id가_없을때(){
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        PartyJoinDto partyJoinDto = new PartyJoinDto(null, user.getId(), guest.get().getId(), PartyJoinStatus.WAIT);

        //when, then
        assertThrows(InvalidDataAccessApiUsageException.class, () -> { //Null 값으로 DB에서 Select를 시도해서 발생
            partyService.joinParty(partyJoinDto);
        });
    }

    //TODO: 실패
    @Test
    void 파티신청_실패_방장Id가_없을때() {
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), null, guest.get().getId(), PartyJoinStatus.WAIT);

        //when, then
        assertThrows(UserException.class, () -> { //Null 값으로 DB에 Insert를 시도해서 발생
            partyService.joinParty(partyJoinDto);
        });
    }

    @Test
    void 파티신청_실패_사용자Id가_없을때(){
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.getId(), null, PartyJoinStatus.WAIT);

        //when, then
        assertThrows(DataIntegrityViolationException.class, () -> {//Null 값으로 DB에 Insert를 시도해서 발생
            partyService.joinParty(partyJoinDto);
        });
    }

    @Test
    void 파티요청_수락_성공() {
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);

        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.getId(), guest.get().getId(), PartyJoinStatus.ACCEPT);
        partyService.joinParty(partyJoinDto);

        //when
        String result = partyService.decideUser(partyJoinDto);
        List<Team> findTeams = teamRepository.findByPartyId(findParty.get().getId());

        //then
        assertThat(result).isEqualTo("Accept Request Completed");
        assertThat(findTeams.size()).isEqualTo(1);
    }

    @Test
    void 파티요청_수락_실패_상태대기() {
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);

        //when
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.getId(), guest.get().getId(), PartyJoinStatus.WAIT);
        partyService.joinParty(partyJoinDto);

        //then
        assertThrows(PartyJoinException.class, () -> partyService.decideUser(partyJoinDto));
    }

    @Test
    void 파티요청_수락_실패_파티없음() {
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);

        //when
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.getId(), guest.get().getId(), PartyJoinStatus.ACCEPT);
        partyService.joinParty(partyJoinDto);
        partyRepository.delete(findParty.get());

        //then
        assertThrows(PartyException.class, () -> partyService.decideUser(partyJoinDto));
    }

    @Test
    void 파티요청_수락_실패_유저없음() {
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);


        //when
        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.getId(), guest.get().getId(), PartyJoinStatus.ACCEPT);
        partyService.joinParty(partyJoinDto);
        userRepository.delete(user);
        userRepository.delete(guest.get());

        //then
        assertThrows(UserException.class, () -> partyService.decideUser(partyJoinDto));
    }

    @Test
    void 파티요청_거절_성공() {
        //given
        Optional<Party> findParty = partyRepository.findById(partyId_list.get(0));
        User user = findParty.get().getUser();
        Optional<User> guest = userRepository.findBySocialId(guestSocialId);

        PartyJoinDto partyJoinDto = new PartyJoinDto(findParty.get().getId(), user.getId(), guest.get().getId(), PartyJoinStatus.REFUSE);
        partyService.joinParty(partyJoinDto);

        //when
        String result = partyService.decideUser(partyJoinDto);
        List<Team> findTeams = teamRepository.findByPartyId(findParty.get().getId());

        //then
        assertThat(result).isEqualTo("Refuse Request Completed");
        assertThat(findTeams.size()).isEqualTo(0);
    }

    @Test
    public void 파티_글_생성_성공() {
        //given
        PartyCreateDto partyCreateDto = createPartyCreateDto();

        assertThat(userRepository.findById(partyCreateDto.getUserId())).isPresent();
        assertThat(partyCreateDto.getTotalParticipant()).isGreaterThanOrEqualTo(2);
        assertThat(partyCreateDto.getDeadline()).isBefore(partyCreateDto.getPartyTime());

        //when
        Map<String, Long> partyId = partyService.createParty(partyCreateDto);

        assertThat(partyId.get("partyId").longValue()).isEqualTo(2L);
    }

    @Test
    public void 파티_글_생성_실패_유저없을때() {
        //given
        PartyCreateDto partyCreateDto = createPartyCreateDto();
        partyCreateDto.setUserId(1234567L);
        //when
        assertThat(userRepository.findById(partyCreateDto.getUserId())).isEqualTo(Optional.empty());
        //Then
        assertThrows(UserException.class, () -> partyService.createParty(partyCreateDto));
    }

    @Test
    public void 파티_글_생성_실패_파티모집인원이_2미만일때() {
        //given
        PartyCreateDto partyCreateDto = createPartyCreateDto();
        partyCreateDto.setTotalParticipant(1);
        //when
        assertThat(partyCreateDto.getTotalParticipant()).isLessThan(2);
        //Then
        assertThrows(PartyException.class, () -> partyService.createParty(partyCreateDto));
    }

    @Test
    public void 파티_글_생성_실패_마감시간이_파티시간보다_늦을때() {
        //given
        PartyCreateDto partyCreateDto = createPartyCreateDto();
        partyCreateDto.setDeadline(LocalDateTime.now().plusHours(12));
        //when
        assertThat(partyCreateDto.getDeadline()).isAfter(partyCreateDto.getPartyTime());
        //Then
        assertThrows(PartyException.class, () -> partyService.createParty(partyCreateDto));
    }

    private PartyCreateDto createPartyCreateDto() {
        PartyCreateDto request = new PartyCreateDto();
        request.setUserId(1L);
        request.setTitle("테스트 파티 생성 DTO");
        request.setContent("파티 생성 테스트");
        request.setLatitude(37.566828706631135);
        request.setLongitude(126.978646598009);
        request.setPartyTime(LocalDateTime.now());
        request.setDeadline(LocalDateTime.now().minusHours(1));
        request.setTotalParticipant(5);
        request.setGender(Gender.ALL);
        request.setAge(PartyAge.AGE2030);
        request.setMenu("TEST");
        request.setCategory(PartyCategory.WESTERN);
        return request;
    }

}