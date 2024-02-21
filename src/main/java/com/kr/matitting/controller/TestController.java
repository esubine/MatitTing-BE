package com.kr.matitting.controller;

import com.kr.matitting.constant.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class TestController {
    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final JwtService jwtService;

    @PostMapping("/matitting777")
    public Map<String, String> dummy() {
        User user = User.builder()
                .socialId("12309812309128301")
                .oauthProvider(OauthProvider.KAKAO)
                .email("test@kakao.com")
                .nickname("새싹개발자")
                .age(20)
                .imgUrl("증명사진100.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();
        User savedUser = userRepository.save(user);
        String accessToken = jwtService.createAccessToken(savedUser);

        Party party = Party.builder()
                .partyTitle("새싹개발자와 돈까스를 먹자!")
                .partyContent("치즈 돈까스 vs 생선 돈까스!")
                .address("서울특별시 마포구 포은로2나길 44")
                .partyPlaceName("크레이지 카츠")
                .longitude(126.90970359894729)
                .latitude(37.55045202364851)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3))
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.ALL)
                .hit(0)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/japanese.jpeg")
                .menu("돈까스")
                .category(PartyCategory.JAPANESE)
                .user(user)
                .build();
        Party savedParty = partyRepository.save(party);

        User user2 = User.builder()
                .socialId("213312213")
                .oauthProvider(OauthProvider.NAVER)
                .email("parksn5029@naver.com")
                .nickname("잔디개발자")
                .age(22)
                .imgUrl("망한사진.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();
        User saved2 = userRepository.save(user2);
        String saved2AccessToken = jwtService.createAccessToken(saved2);

        User user3 = User.builder()
                .socialId("16742355")
                .oauthProvider(OauthProvider.NAVER)
                .email("test@test.com")
                .nickname("잔잔바리")
                .age(22)
                .imgUrl("원숭이.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        User saved3 = userRepository.save(user3);
        String saved3AccessToken = jwtService.createAccessToken(saved3);

        Map<String, String> data = new HashMap<>();
        data.put("지원자 Id", String.valueOf(saved2.getId()));
        data.put("파티 ID", String.valueOf(savedParty.getId()));
        data.put("지원자 token", saved2AccessToken);
        data.put("지원자2 token", saved3AccessToken);
        data.put("방장 token", accessToken);

        return data;
    }
}
