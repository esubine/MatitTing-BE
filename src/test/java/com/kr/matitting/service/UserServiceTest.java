//package com.kr.matitting.service;
//
//import com.kr.matitting.constant.*;
//import com.kr.matitting.dto.PartyCreateDto;
//import com.kr.matitting.dto.UserSignUpDto;
//import com.kr.matitting.dto.UserUpdateDto;
//import com.kr.matitting.entity.Party;
//import com.kr.matitting.entity.Team;
//import com.kr.matitting.entity.User;
//import com.kr.matitting.exception.user.UserException;
//import com.kr.matitting.jwt.service.JwtService;
//import com.kr.matitting.repository.PartyRepository;
//import com.kr.matitting.repository.PartyTeamRepository;
//import com.kr.matitting.repository.UserRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@Transactional
//class UserServiceTest {
//    @Autowired
//    private UserService userService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private JwtService jwtService;
//    @Autowired
//    private PartyRepository partyRepository;
//    @Autowired
//    private PartyTeamRepository teamRepository;
//
//    public static Long partyId;
//    public static List<Long> partyId_list = new ArrayList<>();
//    public static Long userId;
//    @BeforeEach
//    public void 데이터생성() {
//
//        User user = User.builder()
//                .socialId("12345")
//                .socialType(SocialType.KAKAO)
//                .email("test@naver.com")
//                .nickname("새싹개발자")
//                .age(26)
//                .imgUrl("증명사진.jpg")
//                .gender(Gender.MALE)
//                .role(Role.USER)
//                .build();
//        userRepository.save(user);
//
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
//                .age(PartyAge.AGE2030)
//                .hit(0)
//                .menu("치~즈돈까스")
//                .category(PartyCategory.JAPANESE)
//                .user(user)
//                .build();
//
//        Party save = partyRepository.save(party);
//        partyId = save.getId();
//
//        User user1 = User.builder()
//                .socialId("098")
//                .socialType(SocialType.KAKAO)
//                .email("user1@naver.com")
//                .nickname("User1")
//                .age(26)
//                .imgUrl("첫째 증명사진.jpg")
//                .gender(Gender.MALE)
//                .role(Role.USER)
//                .build();
//
//        User user2 = User.builder()
//                .socialId("456")
//                .socialType(SocialType.NAVER)
//                .email("user2@naver.com")
//                .nickname("User2")
//                .age(20)
//                .imgUrl("둘째 증명사진.jpg")
//                .gender(Gender.FEMALE)
//                .role(Role.USER)
//                .build();
//
//        User user3 = User.builder()
//                .socialId("836")
//                .socialType(SocialType.KAKAO)
//                .email("user3@naver.com")
//                .nickname("User3")
//                .age(36)
//                .imgUrl("셋째 증명사진.jpg")
//                .gender(Gender.MALE)
//                .role(Role.USER)
//                .build();
//        User save1 = userRepository.save(user1);
//        User save2 = userRepository.save(user2);
//        User save3 = userRepository.save(user3);
//        userId = save1.getId();
//
//        List<User> users = new ArrayList<>();
//        users.add(user1);
//        users.add(user2);
//        users.add(user3);
//
//        List<String> title_list = new ArrayList<>();
//        title_list.add("서울에서 만나요");
//        title_list.add("수원에서 만나요");
//        title_list.add("전주에서 만나요");
//        title_list.add("광주에서 만나요");
//        title_list.add("전국에서 만나요");
//
//        List<PartyStatus> status_list = new ArrayList<>();
//        status_list.add(PartyStatus.RECRUIT);
//        status_list.add(PartyStatus.RECRUIT);
//        status_list.add(PartyStatus.FINISH);
//        status_list.add(PartyStatus.RECRUIT);
//        status_list.add(PartyStatus.FINISH);
//        status_list.add(PartyStatus.FINISH);
//        status_list.add(PartyStatus.RECRUIT);
//
//        List<String> menu_list = new ArrayList<>();
//        menu_list.add("돈까스");
//        menu_list.add("피자");
//        menu_list.add("치킨");
//        menu_list.add("짜장면");
//        menu_list.add("뇨끼");
//        menu_list.add("김치볶음밥");
//        menu_list.add("삼겹살");
//
//        List<PartyCategory> category_list = new ArrayList<>();
//        category_list.add(PartyCategory.JAPANESE);
//        category_list.add(PartyCategory.WESTERN);
//        category_list.add(PartyCategory.KOREAN);
//        category_list.add(PartyCategory.CHINESE);
//        category_list.add(PartyCategory.WESTERN);
//        category_list.add(PartyCategory.KOREAN);
//        category_list.add(PartyCategory.KOREAN);
//
//        List<LocalDateTime> deadline_list = new ArrayList<>();
//        deadline_list.add(LocalDateTime.now().plusDays(1));
//        deadline_list.add(LocalDateTime.now().plusDays(2));
//        deadline_list.add(LocalDateTime.now().plusDays(3));
//
//        List<LocalDateTime> partyTime_list = new ArrayList<>();
//        partyTime_list.add(LocalDateTime.now().plusDays(3));
//        partyTime_list.add(LocalDateTime.now().plusDays(4));
//        partyTime_list.add(LocalDateTime.now().plusDays(5));
//
//        List<Integer> total_list = new ArrayList<>();
//        total_list.add(3);
//        total_list.add(4);
//        total_list.add(5);
//        total_list.add(6);
//
//        List<Integer> current_list = new ArrayList<>();
//        current_list.add(1);
//        current_list.add(2);
//        current_list.add(3);
//
//        List<Gender> gender = new ArrayList<>();
//        gender.add(Gender.ALL);
//        gender.add(Gender.FEMALE);
//        gender.add(Gender.MALE);
//
//        List<PartyAge> age = new ArrayList<>();
//        age.add(PartyAge.ALL);
//        age.add(PartyAge.AGE2030);
//        age.add(PartyAge.AGE3040);
//        age.add(PartyAge.AGE40);
//
//        Random random = new Random();
//
//        for (int i = 0; i < 20; i++) {
//            Party newParty = Party.builder()
//                    .partyTitle(title_list.get(i % 5)+String.valueOf(i))
//                    .partyContent(random_word(random, 30))
//                    .address(random_word(random, 10))
//                    .latitude(Math.random())
//                    .longitude(Math.random())
//                    .status(status_list.get(i % 7))
//                    .deadline(deadline_list.get(i%3))
//                    .partyTime(partyTime_list.get(i%3))
//                    .totalParticipant(total_list.get(i%4))
//                    .participantCount(current_list.get(i%3))
//                    .gender(gender.get(i%3))
//                    .age(age.get(i%4))
//                    .hit((int) Math.random())
//                    .menu(menu_list.get(i % 7))
//                    .category(category_list.get(i%7))
//                    .user(users.get(i%3))
//                    .build();
//            Party saveParty = partyRepository.save(newParty);
//            partyId_list.add(saveParty.getId());
//
//            Team team = Team.builder().user(newParty.getUser()).party(newParty).role(Role.HOST).build();
//            teamRepository.save(team);
//        }
//    }
//    @AfterEach
//    void clean() {
//        partyId_list.clear();
//    }
//    private String random_word(Random random, int length) {
//        int leftLimit = 48; // numeral '0'
//        int rightLimit = 122; // letter 'z'
//        int targetStringLength = length;
//        return random.ints(leftLimit, rightLimit + 1)
//                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
//                .limit(targetStringLength)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();
//    }
//    @Test
//    void 회원가입_성공() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto("8545232", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//
//        //when
//        User user = userService.signUp(userSignUpDto);
//
//        //then
//        assertThat(user.getId()).isNotNull();
//        assertThat(user.getSocialId()).isEqualTo("8545232");
//        assertThat(user.getSocialType()).isEqualTo(SocialType.KAKAO);
//        assertThat(user.getEmail()).isEqualTo("signUp@naver.com");
//        assertThat(user.getNickname()).isEqualTo("안경잡이개발자");
//        assertThat(user.getAge()).isEqualTo(26);
//        assertThat(user.getImgUrl()).isEqualTo("증명사진.jpg");
//        assertThat(user.getGender()).isEqualTo(Gender.MALE);
//    }
//
//    @Test
//    void 회원가입_실패_socialId_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto(null, SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//
//        //when, then
//        assertThrows(DataIntegrityViolationException.class, () -> userService.signUp(userSignUpDto));
//    }
//
//    @Test
//    void 회원가입_실패_socialType_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto("12345", null, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//
//        //when, then
//        assertThrows(DataIntegrityViolationException.class, () -> userService.signUp(userSignUpDto));
//    }
//
//    @Test
//    void 회원가입_실패_email_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto("12345", SocialType.KAKAO, null, "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//
//        //when, then
//        assertThrows(DataIntegrityViolationException.class, () -> userService.signUp(userSignUpDto));
//    }
//
//    @Test
//    void 회원가입_실패_nickname_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto("12345", SocialType.KAKAO, "signUp@naver.com", null, 26, "증명사진.jpg", Gender.MALE);
//
//        //when, then
//        assertThrows(DataIntegrityViolationException.class, () -> userService.signUp(userSignUpDto));
//    }
//
//    @Test
//    void 회원가입_실패_age_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto(null, SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", null, "증명사진.jpg", Gender.MALE);
//
//        //when, then
//        assertThrows(DataIntegrityViolationException.class, () -> userService.signUp(userSignUpDto));
//    }
//
//    @Test
//    void 회원가입_실패_gender_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto(null, SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", null);
//
//        //when, then
//        assertThrows(DataIntegrityViolationException.class, () -> userService.signUp(userSignUpDto));
//    }
//
//    @Test
//    void 회원탈퇴_성공() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto("4326632362", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        User user = userService.signUp(userSignUpDto);
//        String accessToken = jwtService.createAccessToken(user);
//
//        //when
//        userService.withdraw(accessToken);
//        Optional<User> findUser = userRepository.findById(user.getId());
//
//        //then
//        assertThat(findUser.isEmpty()).isTrue();
//        assertThrows(UserException.class, () -> userService.getMyInfo(user.getSocialType(), user.getSocialId()));
//    }
//
//    @Test
//    void 회원탈퇴_실패_socialId_없음() {
//        //given
//        UserSignUpDto userSignUpDto = new UserSignUpDto("7723732732732", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        User user = userService.signUp(userSignUpDto);
//        User userfake = new UserSignUpDto("23456", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE).toEntity();
//        String accessToken = jwtService.createAccessToken(userfake);
//
//        //when, then
//        assertThrows(UserException.class, () -> userService.withdraw(accessToken));
//    }
//
//    @Test
//    void 프로필_업데이트_성공_사진() {
//        //given
//        String imgUrl = "새로찍은증명사진.jpg";
//        UserSignUpDto userSignUpDto = new UserSignUpDto("6897876768467546754", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        User user = userService.signUp(userSignUpDto);
//
//        //when
//        UserUpdateDto userUpdateDto = new UserUpdateDto(user.getId(), null, imgUrl);
//        userService.update(userUpdateDto);
//
//        //then
//        User finduser = userRepository.findById(user.getId()).get();
//        assertThat(finduser.getImgUrl()).isEqualTo(imgUrl);
//    }
//
//    @Test
//    void 프로필_업데이트_성공_닉네임() {
//        //given
//        String updateNickname = "새싹개발자";
//        UserSignUpDto userSignUpDto = new UserSignUpDto("15145744", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        User user = userService.signUp(userSignUpDto);
//
//        //when
//        UserUpdateDto userUpdateDto = new UserUpdateDto(user.getId(), updateNickname, null);
//        userService.update(userUpdateDto);
//
//        //then
//        User finduser = userRepository.findById(user.getId()).get();
//        assertThat(finduser.getNickname()).isEqualTo(updateNickname);
//    }
//
//    @Test
//    void 프로필_업데이트_실패_ID_Null() {
//        //given
//        String updateNickname = "새싹개발자";
//        UserSignUpDto userSignUpDto = new UserSignUpDto("9888735", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        User user = userService.signUp(userSignUpDto);
//
//        //when, then
//        UserUpdateDto userUpdateDto = new UserUpdateDto(1000L, updateNickname, null);
//        assertThrows(UserException.class, () -> userService.update(userUpdateDto));
//
//    }
//
//    @Test
//    void 파티현황_성공_방장_모집중() {
//        //when
//        List<PartyCreateDto> myPartyList = userService.getMyPartyList(userId, Role.HOST);
//
//        //then
//        assertThat(myPartyList.size()).isEqualTo(4);
//    }
//
//    @Test
//    void 파티현황_성공_지원자_모집중() {
//        //given
//        User user = userRepository.findById(userId).get();
//        Party party_01 = partyRepository.findById(partyId_list.get(1)).get();
//        Party party_02 = partyRepository.findById(partyId_list.get(2)).get();
//        Team team_01 = Team.builder().user(user).party(party_01).role(Role.VOLUNTEER).build();
//        Team team_02 = Team.builder().user(user).party(party_02).role(Role.VOLUNTEER).build();
//        teamRepository.save(team_01);
//        teamRepository.save(team_02);
//
//        //when
//        List<PartyCreateDto> myPartyList = userService.getMyPartyList(userId, Role.VOLUNTEER);
//
//        //then
//        assertThat(myPartyList.size()).isEqualTo(1);
//    }
//
//    @Test
//    void 파티현황_성공_마감() {
//        //when
//        List<PartyCreateDto> myPartyList = userService.getMyPartyList(userId, Role.USER);
//
//        //then
//        assertThat(myPartyList.size()).isEqualTo(3);
//    }
//
//    @Test
//    void 파티현황_실패_ID_없음() {
//        //when
//        List<PartyCreateDto> myPartyList = userService.getMyPartyList(100L, Role.USER);
//
//        //then
//        assertThat(myPartyList.size()).isEqualTo(0);
//    }
//
//    @Test
//    void 파티현황_실패_ID_Null() {
//        //when, then
//        assertThrows(NullPointerException.class, () -> userService.getMyPartyList(null, Role.USER));
//    }
//
//    @Test
//    void 파티현황_실패_Role_잘못됨() {
//        //when, then
//        assertThrows(IllegalArgumentException.class, () -> userService.getMyPartyList(userId, Role.valueOf("213123321132")));
//    }
//
//    @Test
//    void 파티현황_실패_Role_Null() {
//        //when, then
//        assertThrows(NullPointerException.class, () -> userService.getMyPartyList(userId, null));
//    }
//}