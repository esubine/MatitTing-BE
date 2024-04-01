package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.redis.RedisUtil;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.kr.matitting.constant.Gender.*;
import static com.kr.matitting.constant.Gender.ALL;
import static com.kr.matitting.constant.PartyCategory.JAPANESE;
import static com.kr.matitting.constant.PartyCategory.WESTERN;
import static com.kr.matitting.constant.PartyStatus.RECRUIT;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private PartyService partyService;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisUtil redisUtil;

    public User user1;
    public User user2;
    public Party party1;
    public Party party2;
    public Party party3;
    public Party party4;

    @BeforeEach
    void setup() {
        User user1 = User.builder()
                .socialId("1123213321")
                .oauthProvider(OauthProvider.NAVER)
                .email("test@naver.com")
                .nickname("안경잡이개발자")
                .age(20)
                .imgUrl("왈왈.jpg")
                .gender(MALE)
                .role(Role.USER)
                .build();
        this.user1 = userRepository.save(user1);

        User user2 = User.builder()
                .socialId("113929292")
                .oauthProvider(OauthProvider.KAKAO)
                .email("test@kakao.com")
                .nickname("잔디 개발자")
                .age(30)
                .imgUrl("야옹.jpg")
                .gender(FEMALE)
                .role(Role.USER)
                .build();
        this.user2 = userRepository.save(user2);

        Party party1 = Party.builder()
                .partyTitle("맛있팅 첫번째")
                .partyContent("치즈 돈까스 vs 생선 돈까스!")
                .address("서울특별시 마포구 포은로2나길 44")
                .partyPlaceName("크레이지 카츠")
                .longitude(126.90970359894729)
                .latitude(37.55045202364851)
                .status(RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3))
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .participantCount(1)
                .gender(ALL)
                .age(PartyAge.ALL)
                .hit(100)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .menu("돈까스")
                .category(JAPANESE)
                .user(this.user1)
                .build();
        this.party1 = partyRepository.save(party1);

        Party party2 = Party.builder()
                .partyTitle("맛있팅 두번째")
                .partyContent("페페로니 vs 하와이안!")
                .partyPlaceName("피자 파티 투나잇")
                .address("서울특별시 용산구 신흥로 89")
                .longitude(126.9854393172053)
                .latitude(37.545685580653476)
                .status(RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3))
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .participantCount(1)
                .gender(ALL)
                .age(PartyAge.ALL)
                .hit(300)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/western.jpeg")
                .menu("피자")
                .category(WESTERN)
                .user(this.user2)
                .build();
        this.party2 = partyRepository.save(party2);

        Party party3 = Party.builder()
                .partyTitle("맛있팅 세번째")
                .partyContent("페페로니 vs 하와이안!")
                .partyPlaceName("피자 파티 투나잇")
                .address("서울특별시 용산구 신흥로 89")
                .longitude(126.9854393172053)
                .latitude(37.545685580653476)
                .status(RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3))
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .participantCount(1)
                .gender(ALL)
                .age(PartyAge.ALL)
                .hit(200)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/western.jpeg")
                .menu("피자")
                .category(WESTERN)
                .user(this.user1)
                .build();
        this.party3 = partyRepository.save(party3);

        Party party4 = Party.builder()
                .partyTitle("맛있팅 네번째")
                .partyContent("페페로니 vs 하와이안!")
                .partyPlaceName("피자 파티 투나잇")
                .address("서울특별시 용산구 신흥로 89")
                .longitude(126.9854393172053)
                .latitude(37.545685580653476)
                .status(RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3))
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .participantCount(1)
                .gender(ALL)
                .age(PartyAge.ALL)
                .hit(400)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/western.jpeg")
                .menu("피자")
                .category(WESTERN)
                .user(this.user2)
                .build();
        this.party4 = partyRepository.save(party4);
    }

    ResponseCreatePartyJoinDto partyJoin(Long partyId, User volunteer, PartyJoinStatus status) {
        PartyJoinDto partyJoinDto = new PartyJoinDto(partyId, status);
        return partyService.joinParty(partyJoinDto, volunteer);
    }

    ResponseCreatePartyDto partyCreate(User user) {
        PartyCreateDto partyCreateDto = PartyCreateDto
                .builder()
                .partyTitle("테스트 제목")
                .partyContent("테스트 내용")
                .partyPlaceName("가산 인크커피")
                .partyTime(LocalDateTime.now().plusDays(3))
                .totalParticipant(4)
                .longitude(126.88453591058602)
                .latitude(37.53645109566274)
                .gender(MALE)
                .category(WESTERN)
                .age(PartyAge.ALL)
                .menu("커피")
                .thumbnail(null)
                .build();
        return partyService.createParty(user, partyCreateDto);
    }

    @DisplayName("내 프로필 수정 성공")
    @Test
    void 프로필_수정_성공() {
        //given
        UserUpdateDto userUpdateDto = new UserUpdateDto("수정했음", "수정이미지.jpg");

        //when
        userService.update(user1, userUpdateDto);
        Optional<User> byId = userRepository.findById(user1.getId());

        //then
        assertThat(byId).isPresent();
        assertThat(byId.get().getNickname()).isEqualTo("수정했음");
        assertThat(byId.get().getImgUrl()).isEqualTo("수정이미지.jpg");
    }

    @DisplayName("내 프로필 수정 실패 잘못된 사용자 ID")
    @Test
    void 프로필_수정_실패_잘못된_사용자ID() {
        //given
        UserUpdateDto userUpdateDto = new UserUpdateDto("수정했음", "수정이미지.jpg");
        user1.setId(100L);

        //when, then
        assertThrows(UserException.class, () -> userService.update(user1, userUpdateDto));
    }

    @DisplayName("로그아웃 성공")
    @Test
    void 로그아웃_성공() {
        //given
        String accessToken = jwtService.createAccessToken(user1);

        //when
        userService.logout(accessToken, user1);

        //then
        assertThat(redisUtil.getData(accessToken)).isEqualTo("logout");
    }

    @DisplayName("로그아웃 실패 잘못된 Token")
    @Test
    void 로그아웃_실패_잘못된_Token() {
        //given
        String accessToken = "Bearer sfdlkjfsejklfsekjlfesjlkjlksfejlsfeljsljse.fesjisfejifsfesiljsfeljifsjielefs.sfelijsfejilfesjilefsjils";

        //when, then
        assertThrows(TokenException.class, () -> userService.logout(accessToken, user1));
    }

    //회원탈퇴
    @DisplayName("회원탈퇴 성공")
    @Test
    void 회원탈퇴_성공() {
        //given
        String accessToken = jwtService.createAccessToken(user1);

        //when
        userService.withdraw(accessToken, user1);

        //then
        assertThat(redisUtil.getData(accessToken)).isEqualTo("logout");
        assertThat(redisUtil.getData(user1.getSocialId())).isNull();
    }

    @DisplayName("회원탈퇴 실패 잘못된 Token")
    @Test
    void 회원탈퇴_실패_잘못된_Token() {
        //given
        String accessToken = "Bearer sfdlkjfsejklfsekjlfesjlkjlksfejlsfeljsljse.fesjisfejifsfesiljsfeljifsjielefs.sfelijsfejilfesjilefsjils";

        //when, then
        assertThrows(TokenException.class, () -> userService.withdraw(accessToken, user1));
    }

    @DisplayName("내 정보 조회 성공")
    @Test
    void 내정보조회_성공() {
        //when
        ResponseMyInfo myInfo = userService.getMyInfo(user1);

        //then
        assertThat(myInfo.getUserId()).isEqualTo(user1.getId());
        assertThat(myInfo.getSocialId()).isEqualTo(user1.getSocialId());
        assertThat(myInfo.getOauthProvider()).isEqualTo(user1.getOauthProvider());
        assertThat(myInfo.getEmail()).isEqualTo(user1.getEmail());
        assertThat(myInfo.getNickname()).isEqualTo(user1.getNickname());
        assertThat(myInfo.getAge()).isEqualTo(user1.getAge());
        assertThat(myInfo.getImgUrl()).isEqualTo(user1.getImgUrl());
        assertThat(myInfo.getGender()).isEqualTo(user1.getGender());
        assertThat(myInfo.getRole()).isEqualTo(user1.getRole());
    }

    @DisplayName("내 파티 리스트 조회 성공")
    @Test
    void 파티_리스트_조회_성공() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto1 = partyCreate(user1);
        ResponseCreatePartyDto responseCreatePartyDto2 = partyCreate(user1);

        ResponseCreatePartyDto responseCreatePartyDto3 = partyCreate(user2);
        ResponseCreatePartyDto responseCreatePartyDto4 = partyCreate(user2);

        ResponseCreatePartyJoinDto responseCreatePartyJoinDto1 = partyJoin(responseCreatePartyDto3.getPartyId(), user1, PartyJoinStatus.APPLY);
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto2 = partyJoin(responseCreatePartyDto4.getPartyId(), user1, PartyJoinStatus.APPLY);
        PartyDecisionDto partyDecisionDto1 = new PartyDecisionDto(responseCreatePartyDto3.getPartyId(), user1.getNickname(), PartyDecision.ACCEPT);
        PartyDecisionDto partyDecisionDto2 = new PartyDecisionDto(responseCreatePartyDto4.getPartyId(), user1.getNickname(), PartyDecision.ACCEPT);
        partyService.decideUser(partyDecisionDto1, user2);
        partyService.decideUser(partyDecisionDto2, user2);

        //when
        List<ResponsePartyDto> myPartyList = userService.getMyPartyList(user1, Role.HOST);
        List<ResponsePartyDto> myPartyList1 = userService.getMyPartyList(user1, Role.VOLUNTEER);

        //then
        assertThat(myPartyList.size()).isEqualTo(2);
        assertThat(myPartyList1.size()).isEqualTo(2);
    }

    @DisplayName("내 파티 리스트 조회 실패 유효하지 않은 Role")
    @Test
    void 파티_리스트_조회_실패_유효하지않은Role() {
        //when, then
        assertThrows(IllegalArgumentException.class, ()->userService.getMyPartyList(user1, Role.valueOf("test")));
    }
}