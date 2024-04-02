package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.partyjoin.PartyJoinException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.kr.matitting.constant.Gender.*;
import static com.kr.matitting.constant.PartyCategory.JAPANESE;
import static com.kr.matitting.constant.PartyCategory.WESTERN;
import static com.kr.matitting.constant.PartyStatus.PARTY_FINISH;
import static com.kr.matitting.constant.PartyStatus.RECRUIT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PartyServiceTest {

    @Autowired
    private PartyService partyService;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ReviewService reviewService;

    //사용자 1
    public User user1;
    //사용자 2
    public User user2;
    public Party party1;
    public Party party2;

    @BeforeEach
    void setup() {
        User user1 = new User("1123213321",
                OauthProvider.NAVER,
                "test@naver.com",
                "안경잡이개발자",
                20,
                null,
                MALE,
                Role.USER
        );
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
                .receivedReviews(new ArrayList<>())
                .sendReviews(new ArrayList<>())
                .build();
        this.user2 = userRepository.save(user2);

        Party party1 = Party.builder()
                .partyTitle("새싹개발자와 돈까스를 먹자!")
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
                .hit(0)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .menu("돈까스")
                .category(JAPANESE)
                .user(this.user1)
                .build();
        this.party1 = partyRepository.save(party1);

        Party party2 = Party.builder()
                .partyTitle("잔디개발자와 피자를 먹자!")
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
                .hit(0)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/western.jpeg")
                .menu("피자")
                .category(WESTERN)
                .user(this.user2)
                .build();
        this.party2 = partyRepository.save(party2);
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

    ResponseCreatePartyJoinDto partyJoin(Long partyId, User volunteer, PartyJoinStatus status, String oneLineIntroduce) {
        PartyJoinDto partyJoinDto = new PartyJoinDto(partyId, status, oneLineIntroduce);
        return partyService.joinParty(partyJoinDto, volunteer);
    }

    @DisplayName("파티 정보 불러오기 성공")
    @Test
    void 파티_조회_성공() {
        //when
        party1.setPartyTime(LocalDateTime.now().minusHours(5));
        ReviewCreateReq reviewCreateReq = new ReviewCreateReq(user1.getId(), party1.getId(), "멋져요", 50, List.of("단체사진.jpg"));
        reviewService.createReview(reviewCreateReq, user2);

        ResponsePartyDetailDto partyInfo = partyService.getPartyInfo(user1, party1.getId());
        ResponsePartyDetailDto partyInfo1 = partyService.getPartyInfo(user1, party2.getId());

        //then
        assertThat(partyInfo.getPartyId()).isEqualTo(party1.getId());
        assertThat(partyInfo.getIsLeader()).isTrue();
        assertThat(partyInfo.getPartyTitle()).isEqualTo("새싹개발자와 돈까스를 먹자!");
        assertThat(partyInfo.getPartyContent()).isEqualTo("치즈 돈까스 vs 생선 돈까스!");
        assertThat(partyInfo.getAddress()).isEqualTo("서울특별시 마포구 포은로2나길 44");
        assertThat(partyInfo.getPartyPlaceName()).isEqualTo("크레이지 카츠");
        assertThat(partyInfo.getStatus()).isEqualTo(RECRUIT);
        assertThat(partyInfo.getGender()).isEqualTo(ALL);
        assertThat(partyInfo.getAge()).isEqualTo(PartyAge.ALL);
        assertThat(partyInfo.getTotalParticipant()).isEqualTo(4);
        assertThat(partyInfo.getParticipate()).isEqualTo(1);
        assertThat(partyInfo.getMenu()).isEqualTo("돈까스");
        assertThat(partyInfo.getCategory()).isEqualTo(JAPANESE);
        assertThat(partyInfo.getHit()).isEqualTo(0);
        assertThat(partyInfo.getReviewInfoRes().size()).isEqualTo(1);
        assertThat(partyInfo.getReviewInfoRes().get(0).getRating()).isEqualTo(50);

        assertThat(partyInfo1.getIsLeader()).isFalse();
    }

    @DisplayName("파티 정보 불러오기 실패 없는 파티 ID")
    @Test
    void 파티_조회_실패_없는_파티ID() {
        //when, then
        assertThrows(PartyException.class, () -> partyService.getPartyInfo(user1, party1.getId()+2L));
    }

    @DisplayName("파티 수정 성공")
    @Test
    void 파티_수정_성공() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto("제목수정", "내용수정", "메뉴수정", null, null, "인계지음 605호", PARTY_FINISH, 2, FEMALE, PartyAge.TWENTY, null, null);

        //when
//        partyService.partyUpdate(user1, partyUpdateDto, party1.getId());
        ResponsePartyDetailDto partyInfo = partyService.getPartyInfo(user1, party1.getId());

        //then
        assertThat(partyInfo.getIsLeader()).isTrue();
        assertThat(partyInfo.getPartyTitle()).isEqualTo("제목수정");
        assertThat(partyInfo.getPartyContent()).isEqualTo("내용수정");
        assertThat(partyInfo.getPartyPlaceName()).isEqualTo("인계지음 605호");
        assertThat(partyInfo.getStatus()).isEqualTo(PARTY_FINISH);
        assertThat(partyInfo.getGender()).isEqualTo(FEMALE);
        assertThat(partyInfo.getAge()).isEqualTo(PartyAge.TWENTY);
        assertThat(partyInfo.getTotalParticipant()).isEqualTo(2);
        assertThat(partyInfo.getMenu()).isEqualTo("메뉴수정");
        assertThat(partyInfo.getCategory()).isEqualTo(JAPANESE);
    }

    @DisplayName("파티 수정 실패 권한 없음")
    @Test
    void 파티_수정_실패_권한없음() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto("제목수정", "내용수정", "메뉴수정", null, null, "인계지음 605호", PARTY_FINISH, 2, FEMALE, PartyAge.TWENTY, null, null);

        //when, then
//        assertThrows(UserException.class, () -> partyService.partyUpdate(user2, partyUpdateDto, party1.getId()));
    }

    @DisplayName("파티 수정 실패 잘못된 파티ID")
    @Test
    void 파티_수정_실패_없는_파티ID() {
        //given
        PartyUpdateDto partyUpdateDto = new PartyUpdateDto("제목수정", "내용수정", "메뉴수정", null, null, "인계지음 605호", PARTY_FINISH, 2, FEMALE, PartyAge.TWENTY, null, null);

        //when, then
//        assertThrows(PartyException.class, () -> partyService.partyUpdate(user1, partyUpdateDto, party1.getId()+100L));
    }

    @DisplayName("파티 삭제 성공")
    @Test
    void 파티_삭제_성공() {
        //when
        partyService.deleteParty(user1, party1.getId());

        //then
        assertThrows(PartyException.class, () -> partyService.getPartyInfo(user1, party1.getId()));
    }

    @DisplayName("파티 삭제 실패 권한 없음")
    @Test
    void 파티_삭제_실패_권한없음() {
        //when, then
        assertThrows(UserException.class, () -> partyService.deleteParty(user2, party1.getId()));
    }

    @DisplayName("파티 삭제 실패 없는 파티 ID")
    @Test
    void 파티_삭제_실패_없는_파티ID() {
        //when, then
        assertThrows(PartyException.class, () -> partyService.deleteParty(user2, party1.getId()+100L));
    }

    @DisplayName("파티 신청 성공")
    @Test
    void 파티_신청_성공() {
        //given
        PartyJoinDto partyJoinDto = new PartyJoinDto(party2.getId(), PartyJoinStatus.APPLY, "안녕하세요");

        //when
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto = partyService.joinParty(partyJoinDto, user1);

        //then
        assertThat(responseCreatePartyJoinDto.getPartyJoinId()).isNotNull();
    }

    @DisplayName("파티 신청 실패 본인 파티에 신청")
    @Test
    void 파티_신청_실패_본인파티에신청() {
        //given
        PartyJoinDto partyJoinDto = new PartyJoinDto(party1.getId(), PartyJoinStatus.APPLY, "안녕하세요");

        //when, then
        assertThrows(UserException.class, () -> partyService.joinParty(partyJoinDto, user1));
    }

    @DisplayName("파티 신청 실패 중복 신청")
    @Test
    void 파티_신청_실패_중복신청() {
        //given
        PartyJoinDto partyJoinDto = new PartyJoinDto(party2.getId(), PartyJoinStatus.APPLY, "안녕하세요");

        //when
        partyService.joinParty(partyJoinDto, user1);

        //then
        assertThrows(PartyJoinException.class, () -> partyService.joinParty(partyJoinDto, user1));
    }

    @DisplayName("파티 신청 취소 성공")
    @Test
    void 파티_신청취소_성공() {
        //given
        PartyJoinDto partyJoinDto = new PartyJoinDto(party2.getId(), PartyJoinStatus.APPLY, "안녕하세요");
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto = partyService.joinParty(partyJoinDto, user1);

        //when
        PartyJoinDto partyJoinDto1 = new PartyJoinDto(party2.getId(), PartyJoinStatus.CANCEL, null);
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto1 = partyService.joinParty(partyJoinDto1, user1);

        //then
        assertThat(responseCreatePartyJoinDto1.getPartyJoinId()).isNotNull();
    }

    @DisplayName("파티 신청 취소 실패 신청한 적 없음")
    @Test
    void 파티_신청취소_실패_신청한적_없음() {
        //given
        PartyJoinDto partyJoinDto1 = new PartyJoinDto(party2.getId(), PartyJoinStatus.CANCEL, null);

        //when, then
        assertThrows(PartyJoinException.class, () -> partyService.joinParty(partyJoinDto1, user1));
    }

    @DisplayName("파티 신청 실패 신청 Type이 잘못됌")
    @Test
    void 파티_신청취소_실패_TypeMiss() {
        //when, then
        assertThrows(IllegalArgumentException.class, () -> new PartyJoinDto(party2.getId(), PartyJoinStatus.valueOf("MISS"), "안녕하세요"));
    }

    @DisplayName("파티 승락 성공")
    @Test
    void 파티_승락_성공() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "안녕하세요");
        PartyDecisionDto partyDecisionDto = new PartyDecisionDto(responseCreatePartyDto.getPartyId(), "잔디 개발자", PartyDecision.ACCEPT);

        //when
        String result = partyService.decideUser(partyDecisionDto, user1);

        //then
        assertThat(result).isEqualTo("Accept Request Completed");
    }

    @DisplayName("파티 승락 실패 신청 조회 실패")
    @Test
    void 파티_승락_실패_신청조회_실패() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "안녕하세요");
        PartyDecisionDto partyDecisionDto = new PartyDecisionDto(responseCreatePartyDto.getPartyId() + 2L, "잔디 개발자", PartyDecision.ACCEPT);

        //when, then
        assertThrows(PartyJoinException.class, () -> partyService.decideUser(partyDecisionDto, user1));
    }

    @DisplayName("파티 거절 성공")
    @Test
    void 파티_거절_성공() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "안녕하세요");
        PartyDecisionDto partyDecisionDto = new PartyDecisionDto(responseCreatePartyDto.getPartyId(), "잔디 개발자", PartyDecision.REFUSE);

        //when
        String result = partyService.decideUser(partyDecisionDto, user1);

        //then
        assertThat(result).isEqualTo("Refuse Request Completed");
    }

    @DisplayName("파티 신청 현황 조회 성공 - HOST")
    @Test
    void 파티_신청현황_HOST_조회_성공() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        ResponseCreatePartyDto responseCreatePartyDto2 = partyCreate(user2);
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto = partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "봉쥬르");
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto1 = partyJoin(responseCreatePartyDto2.getPartyId(), user1, PartyJoinStatus.APPLY, "곤니찌와");

        //when
        ResponseGetPartyJoinDto joinList = partyService.getJoinList(user1, Role.HOST, 1, 0L);

        //then
        assertThat(joinList.getPartyList().size()).isEqualTo(1);
        assertThat(joinList.getPartyList().get(0).getNickname()).isEqualTo("잔디 개발자");
        assertThat(joinList.getPartyList().get(0).getPartyGender()).isEqualTo(MALE);
        assertThat(joinList.getPartyList().get(0).getOneLineIntroduce()).isEqualTo("봉쥬르");
        assertThat(joinList.getPageInfo().isHasNext()).isFalse();
        assertThat(joinList.getPageInfo().getLastId()).isEqualTo(responseCreatePartyJoinDto.getPartyJoinId());
    }

    @DisplayName("파티 신청 현황 조회 성공 - VOLUNTEER")
    @Test
    void 파티_신청현황_VOLUNTEER_조회_성공() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        ResponseCreatePartyDto responseCreatePartyDto2 = partyCreate(user2);
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto = partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "안녕하세요");
        ResponseCreatePartyJoinDto responseCreatePartyJoinDto1 = partyJoin(responseCreatePartyDto2.getPartyId(), user1, PartyJoinStatus.APPLY, "안녕하세요");

        //when
        ResponseGetPartyJoinDto joinList = partyService.getJoinList(user1, Role.VOLUNTEER, 1, 0L);

        //then
        assertThat(joinList.getPartyList().size()).isEqualTo(1);
        assertThat(joinList.getPartyList().get(0).getNickname()).isNull();
        assertThat(joinList.getPartyList().get(0).getPartyGender()).isEqualTo(MALE);
        assertThat(joinList.getPageInfo().isHasNext()).isFalse();
        assertThat(joinList.getPageInfo().getLastId()).isEqualTo(responseCreatePartyJoinDto1.getPartyJoinId());
    }

    @DisplayName("파티 신청 현황 조회 실패 잘못된 Role")
    @Test
    void 파티_신청현황_조회_실패_잘못된_Role() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        ResponseCreatePartyDto responseCreatePartyDto2 = partyCreate(user2);
        partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "안녕하세요");
        partyJoin(responseCreatePartyDto2.getPartyId(), user1, PartyJoinStatus.APPLY, "안녕하세요");

        //when, then
        assertThrows(IllegalArgumentException.class, () -> partyService.getJoinList(user1, Role.valueOf("check"), 1, 0L));
    }

    @DisplayName("파티 신청 현황 조회 실패 없는 사용자")
    @Test
    void 파티_신청현황_조회_실패_없는_사용자() {
        //given
        ResponseCreatePartyDto responseCreatePartyDto = partyCreate(user1);
        ResponseCreatePartyDto responseCreatePartyDto2 = partyCreate(user2);
        partyJoin(responseCreatePartyDto.getPartyId(), user2, PartyJoinStatus.APPLY, "안녕하세요");
        partyJoin(responseCreatePartyDto2.getPartyId(), user1, PartyJoinStatus.APPLY, "안녕하세요");

        userRepository.deleteById(user1.getId());
        userRepository.deleteById(user2.getId());

        //when, then
        assertThrows(UserException.class, () -> partyService.getJoinList(user1, Role.HOST, 1, 0L));
        assertThrows(UserException.class, () -> partyService.getJoinList(user1, Role.VOLUNTEER, 1, 0L));
    }
}