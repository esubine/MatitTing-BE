package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.dto.ResponseSearchPageDto;
import com.kr.matitting.dto.SortDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.kr.matitting.constant.Gender.*;
import static com.kr.matitting.constant.Gender.ALL;
import static com.kr.matitting.constant.PartyCategory.JAPANESE;
import static com.kr.matitting.constant.PartyCategory.WESTERN;
import static com.kr.matitting.constant.PartyStatus.RECRUIT;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class SearchServiceTest {
    @Autowired
    private SearchService searchService;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserRepository userRepository;

    public User user1;
    public User user2;
    public Party party1;
    public Party party2;
    public Party party3;
    public Party party4;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setupRedis() {
        // Redis 데이터 초기화
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

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

    @DisplayName("파티 검색 성공")
    @Test
    void 파티_검색_성공() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("맛있팅", RECRUIT, new SortDto(Sorts.HIT, Orders.DESC));

        //when
        ResponseSearchPageDto partyPage = searchService.getPartyPage(partySearchCondDto, 2, 0L);
        ResponseSearchPageDto partyPage1 = searchService.getPartyPage(partySearchCondDto, 2, partyPage.getLastPartyId());

        //then
        assertThat(partyPage.getPartyList().size()).isEqualTo(4);
        assertThat(partyPage.getHasNext()).isFalse();
        assertThat(partyPage.getLastPartyId()).isEqualTo(party4.getId());
        assertThat(partyPage1.getPartyList().size()).isEqualTo(2);
        assertThat(partyPage1.getHasNext()).isTrue();
        assertThat(partyPage1.getLastPartyId()).isEqualTo(party3.getId());

    }

    @DisplayName("인기 검색어 조회 성공")
    @Test
    void 인기검색어_조회_성공() {
        //given
        PartySearchCondDto partySearchCondDto1 = new PartySearchCondDto("맛있팅", RECRUIT, new SortDto(Sorts.HIT, Orders.ASC));
        PartySearchCondDto partySearchCondDto2 = new PartySearchCondDto("맛있팅", RECRUIT, new SortDto(Sorts.HIT, Orders.ASC));
        PartySearchCondDto partySearchCondDto3 = new PartySearchCondDto("맛있팅", RECRUIT, new SortDto(Sorts.HIT, Orders.ASC));
        PartySearchCondDto partySearchCondDto4 = new PartySearchCondDto("우수", RECRUIT, new SortDto(Sorts.HIT, Orders.ASC));
        PartySearchCondDto partySearchCondDto5 = new PartySearchCondDto("우수", RECRUIT, new SortDto(Sorts.HIT, Orders.ASC));
        PartySearchCondDto partySearchCondDto6 = new PartySearchCondDto("양양", RECRUIT, new SortDto(Sorts.HIT, Orders.ASC));
        searchService.getPartyPage(partySearchCondDto1, 4, 0L);
        searchService.getPartyPage(partySearchCondDto2, 4, 0L);
        searchService.getPartyPage(partySearchCondDto3, 4, 0L);
        searchService.getPartyPage(partySearchCondDto4, 4, 0L);
        searchService.getPartyPage(partySearchCondDto5, 4, 0L);
        searchService.getPartyPage(partySearchCondDto6, 4, 0L);

        //when
        List<ResponseRankingDto> responseRankingDtos = searchService.searchRankList();

        //then
        assertThat(responseRankingDtos.size()).isEqualTo(3);
        assertThat(responseRankingDtos.get(0).getKeyword()).isEqualTo("맛있팅");
        assertThat(responseRankingDtos.get(1).getKeyword()).isEqualTo(("우수"));
        assertThat(responseRankingDtos.get(2).getKeyword()).isEqualTo(("양양"));
    }
}