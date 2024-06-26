package com.kr.matitting.service;

import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.constant.PartyAge;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.Sorts;
import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.ResponseCreatePartyDto;
import com.kr.matitting.dto.ResponseMainPageDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.kr.matitting.constant.Gender.*;
import static com.kr.matitting.constant.PartyCategory.JAPANESE;
import static com.kr.matitting.constant.PartyStatus.RECRUIT;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MainServiceTest {
    @Autowired
    private MainService mainService;
    @Autowired
    private PartyService partyService;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserRepository userRepository;

    public User user1;
    //사용자 2
    public User user2;
    public Party party1;
    public Party party2;
    public Party party3;
    public Party party4;
    public Party party5;
    public Party party6;

    @BeforeEach
    void setup() {
        partyRepository.deleteAll();

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

        PartyCreateDto partyCreateDto1 = PartyCreateDto.builder()
                .partyTitle("맛있팅 1")
                .partyContent("합정 크레이지 카츠")
                .partyPlaceName("합정 크레이지 카츠")
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .longitude(126.9097)
                .latitude(37.55046)
                .gender(ALL)
                .category(JAPANESE)
                .age(PartyAge.ALL)
                .menu("돈까스")
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .build();
        ResponseCreatePartyDto party1 = partyService.createParty(user1.getId(), partyCreateDto1);
        this.party1 = partyRepository.findById(party1.getPartyId()).get();
        
        PartyCreateDto partyCreateDto2 = PartyCreateDto.builder()
                .partyTitle("맛있팅 2")
                .partyContent("합정 빕스")
                .partyPlaceName("합정 빕스")
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .longitude(126.9122)
                .latitude(37.54977)
                .gender(ALL)
                .category(JAPANESE)
                .age(PartyAge.ALL)
                .menu("뷔페")
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .build();
        ResponseCreatePartyDto party2 = partyService.createParty(user1.getId(), partyCreateDto2);
        this.party2 = partyRepository.findById(party2.getPartyId()).get();

        PartyCreateDto partyCreateDto3 = PartyCreateDto.builder()
                .partyTitle("맛있팅 3")
                .partyContent("용산 몽탄")
                .partyPlaceName("용산 몽탄")
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .longitude(126.9722)
                .latitude(37.53615)
                .gender(ALL)
                .category(JAPANESE)
                .age(PartyAge.ALL)
                .menu("고기")
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .build();
        ResponseCreatePartyDto party3 = partyService.createParty(user1.getId(), partyCreateDto3);
        this.party3 = partyRepository.findById(party3.getPartyId()).get();

        PartyCreateDto partyCreateDto4 = PartyCreateDto.builder()
                .partyTitle("맛있팅 4")
                .partyContent("용산 현선이네")
                .partyPlaceName("용산 현선이네")
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .longitude(126.9692)
                .latitude(37.53131)
                .gender(ALL)
                .category(JAPANESE)
                .age(PartyAge.ALL)
                .menu("떡볶이")
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .build();
        ResponseCreatePartyDto party4 = partyService.createParty(user1.getId(), partyCreateDto4);
        this.party4 = partyRepository.findById(party4.getPartyId()).get();

        PartyCreateDto partyCreateDto5 = PartyCreateDto.builder()
                .partyTitle("맛있팅 5")
                .partyContent("강남 마녀주방")
                .partyPlaceName("강남 마녀주방")
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .longitude(127.0282)
                .latitude(37.49957)
                .gender(ALL)
                .category(JAPANESE)
                .age(PartyAge.ALL)
                .menu("피자")
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .build();
        ResponseCreatePartyDto party5 = partyService.createParty(user1.getId(), partyCreateDto5);
        this.party5 = partyRepository.findById(party5.getPartyId()).get();

        PartyCreateDto partyCreateDto6 = PartyCreateDto.builder()
                .partyTitle("맛있팅 6")
                .partyContent("강남 갓덴스시")
                .partyPlaceName("강남 갓덴스시")
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .longitude(127.0290)
                .latitude(37.49901)
                .gender(ALL)
                .category(JAPANESE)
                .age(PartyAge.ALL)
                .menu("스시")
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .build();
        ResponseCreatePartyDto party6 = partyService.createParty(user1.getId(), partyCreateDto6);
        this.party6 = partyRepository.findById(party6.getPartyId()).get();
    }

    @DisplayName("메인 홈 성공 - 거리순 정렬")
    @Test
    void 메인_홈_성공_거리순() {
        //given
        MainPageDto mainPageDto = new MainPageDto(126.9583, 37.53471, RECRUIT, null);
        Pageable pageable = PageRequest.of(0, 3);
        ResponseMainPageDto partyList = mainService.getPartyList(mainPageDto, pageable);

        //when
        MainPageDto mainPageDto2 = new MainPageDto(126.9583, 37.53471, RECRUIT, null);
        Pageable pageable2 = PageRequest.of(1, 3);
        ResponseMainPageDto partyList2 = mainService.getPartyList(mainPageDto2, pageable2);

        //then
        assertThat(partyList.getPartyList().size()).isEqualTo(3);
        assertThat(partyList.getPageInfo().isHasNext()).isTrue();
        assertThat(partyList2.getPartyList().size()).isEqualTo(3);
        assertThat(partyList2.getPageInfo().isHasNext()).isFalse();
    }

    @DisplayName("메인 홈 성공 - 최신순")
    @Test
    void 메인_홈_성공_최신순() {
        //given
        MainPageDto mainPageDto = new MainPageDto(126.9583, 37.53471, RECRUIT, Sorts.LATEST);
        Pageable pageable = PageRequest.of(0, 3);

        //when
        ResponseMainPageDto partyList = mainService.getPartyList(mainPageDto, pageable);

        //then
        assertThat(partyList.getPartyList().size()).isEqualTo(3);
        assertThat(partyList.getPageInfo().isHasNext()).isTrue();
        assertThat(partyList.getPartyList().get(0).partyId()).isEqualTo(party6.getId());
        assertThat(partyList.getPartyList().get(1).partyId()).isEqualTo(party5.getId());
        assertThat(partyList.getPartyList().get(2).partyId()).isEqualTo(party4.getId());
    }
}