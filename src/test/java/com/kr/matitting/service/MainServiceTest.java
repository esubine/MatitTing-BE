package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyRepositoryImpl;
import com.kr.matitting.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class MainServiceTest {
    @Autowired
    private MainService mainService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyRepository partyRepository;

    @BeforeEach
    public void 데이터생성() {
        User user = User.builder()
                .socialId("30123")
                .email("lsb1026@naver.com")
                .nickname("subin")
                .age(26)
                .gender(Gender.ALL)
                .imgUrl("https://www.naver.com")
                .role(Role.USER)
                .oauthProvider(OauthProvider.NAVER)
                .build();
        userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            Party party = Party.builder()
                    .partyTitle("파티 TEST" + i)
                    .partyContent(i + "번째 party")
                    .totalParticipant(4)
                    .address(i + "번째 파티 주소")
                    .partyTime(LocalDateTime.of(2023, 10, 12, 15, 23, 32))
                    .menu("메뉴")
                    .longitude(12.111)
                    .latitude(37.111)
                    .age(PartyAge.AGE2030)
                    .category(PartyCategory.CHINESE)
                    .status(PartyStatus.RECRUIT)
                    .gender(Gender.ALL)
                    .user(user)
                    .deadline(LocalDateTime.of(2023, 10, 12, 15, 23, 32))
                    .hit(1)
                    .build();
            partyRepository.save(party);
        }

    }

    @Test
    public void 메인페이지_조회_성공() {
        //given
        MainPageDto mainPageDto = new MainPageDto(37.566828706631135, 126.978646598009);
        Pageable pageable = PageRequest.of(0, 10);

        List<Party> partyList = new ArrayList<>();

        PartyRepositoryImpl partyRepository = Mockito.mock(PartyRepositoryImpl.class);
        Mockito.when(partyRepository.getPartyList(Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.anyDouble(), Mockito.any(Pageable.class)))
                .thenReturn(partyList);

        List<ResponsePartyDto> result = mainService.getPartyList(mainPageDto, pageable);

        assertEquals(partyList.size(), result.size());
    }
}
