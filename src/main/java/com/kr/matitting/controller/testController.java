package com.kr.matitting.controller;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.ResponseDummyDataDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.PartyJoinRepository;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class testController {

    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final PartyJoinRepository partyJoinRepository;
    private final JwtService jwtService;

    @GetMapping("/home")
    public String home(Model model) {
        return "home";
    }

    @Operation(summary = "유저 더미데이터 생성 1", description = "유저 더미데이터 생성 API \n\n" +
                                                        "해당 API 요청 시 유저 1명이 생성됩니다.\n\n"
    )
    @GetMapping("/matitting")
    public ResponseEntity<User> dummy_data(HttpServletResponse response) {
        Optional<User> findUser = userRepository.findBySocialId("12309812309128301");

        if (findUser.isEmpty()) {
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
            userRepository.save(user);
        }

        User user = userRepository.findBySocialId("12309812309128301").get();
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        return ResponseEntity.ok(user);
    }
    @Operation(summary = "유저 더미데이터 생성 2", description = "유저 더미데이터 생성 API \n\n" +
                                                            "해당 API 요청 시 유저 1명이 생성됩니다. \n\n"
    )
    @GetMapping("/matitting2")
    public ResponseEntity<User> dummy_data2(HttpServletResponse response) {
        Optional<User> findUser = userRepository.findBySocialId("1123213321");

        if (findUser.isEmpty()) {
            User user = User.builder()
                    .socialId("1123213321")
                    .oauthProvider(OauthProvider.NAVER)
                    .email("test@naver.com")
                    .nickname("안경잡이개발자")
                    .age(20)
                    .imgUrl("증명사진50.jpg")
                    .gender(Gender.MALE)
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        User user = userRepository.findBySocialId("1123213321").get();
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtService.getRefreshHeader(), "Bearer " + refreshToken);

        return ResponseEntity.ok(user);
    }
    @Operation(summary = "유저, 파티 더미데이터 생성", description = "유저, 파티 더미데이터 생성 API 입니다. \n\n" +
                                                    "해당 API 요청 완료 시 \n\n" +
                                                    "1. 사용자 1 생성\n\n" +
                                                    "    - 파티 생성\n\n" +
                                                    "    - 사용자2 파티에 참가 신청\n\n" +
                                                    "2. 사용자 2 생성\n\n" +
                                                    "    - 파티 생성\n\n" +
                                                    "    - 사용자1 파티에 참가 신청 \n\n"
    )
    @GetMapping("/matitting3")
    public ResponseEntity<ResponseDummyDataDto> test(HttpServletResponse response) {
        Optional<User> findUser = userRepository.findBySocialId("3035953918");

        if(findUser.isEmpty()){
            User user1 = User.builder()
                    .socialId("3035953918")
                    .oauthProvider(OauthProvider.KAKAO)
                    .email("parksn5029@nate.com")
                    .nickname("나무개발자")
                    .age(26)
                    .imgUrl("증명사진100.jpg")
                    .gender(Gender.MALE)
                    .role(Role.USER)
                    .build();
            User saved1 = userRepository.save(user1);
        }
        User user1 = User.builder()
                .socialId("3035953918")
                .oauthProvider(OauthProvider.KAKAO)
                .email("parksn5029@nate.com")
                .nickname("나무개발자")
                .age(26)
                .imgUrl("증명사진100.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        User saved1 = userRepository.save(user1);
        String saved1AccessToken = jwtService.createAccessToken(saved1);
        String saved1refreshToken = jwtService.createRefreshToken(saved1);

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
        String saved2refreshToken = jwtService.createRefreshToken(saved2);

        Party party1 = Party.builder()
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
                .user(user1)
                .build();
        partyRepository.save(party1);
        Long party1Id = party1.getId();

        Party party2 = Party.builder()
                .partyTitle("잔디개발자와 피자를 먹자!")
                .partyContent("페페로니 vs 하와이안!")
                .partyPlaceName("피자 파티 투나잇")
                .address("서울특별시 용산구 신흥로 89")
                .longitude(126.9854393172053)
                .latitude(37.545685580653476)
                .status(PartyStatus.RECRUIT)
                .deadline(LocalDateTime.now().plusDays(3))
                .partyTime(LocalDateTime.now().plusDays(3).plusHours(1))
                .totalParticipant(4)
                .participantCount(1)
                .gender(Gender.ALL)
                .age(PartyAge.ALL)
                .hit(0)
                .thumbnail("https://matitting.s3.ap-northeast-2.amazonaws.com/western.jpeg")
                .menu("피자")
                .category(PartyCategory.WESTERN)
                .user(user2)
                .build();
        partyRepository.save(party2);
        Long party2Id = party2.getId();

        PartyJoin partyJoin1 = PartyJoin.builder()
                .party(party1)
                .leaderId(saved1.getId())
                .userId(saved2.getId())
                .build();
        partyJoinRepository.save(partyJoin1);

        PartyJoin partyJoin2 = PartyJoin.builder()
                .party(party2)
                .leaderId(saved2.getId())
                .userId(saved1.getId())
                .build();
        partyJoinRepository.save(partyJoin2);
        return ResponseEntity.ok(new ResponseDummyDataDto(saved1AccessToken, saved1refreshToken, saved2AccessToken, saved2refreshToken, party1Id, party2Id));
    }
}