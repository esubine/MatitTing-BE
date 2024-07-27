package com.kr.matitting.controller;

import ch.qos.logback.core.model.Model;
import com.kr.matitting.constant.*;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.PartyJoin;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.*;
import com.kr.matitting.service.PartyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class testController {

    private final UserRepository userRepository;
    private final PartyRepository partyRepository;
    private final PartyJoinRepository partyJoinRepository;
    private final PartyTeamRepository teamRepository;
    private final PartyService partyService;
    private final ReviewRepository reviewRepository;
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatUserRepository chatUserRepository;
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
    @PostMapping("/matitting3")
    public ResponseEntity<ResponseDummyDataDto> test(HttpServletResponse response) {
        Optional<User> bySocialId_01 = userRepository.findBySocialId("3035953918");
        Optional<User> bySocialId_02 = userRepository.findBySocialId("213312213");

        bySocialId_01.ifPresent(user -> userRepository.deleteById(user.getId()));
        bySocialId_02.ifPresent(user -> userRepository.deleteById(user.getId()));

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
        String saved1AccessToken = "Bearer " + jwtService.createAccessToken(saved1);
        String saved1refreshToken = "Bearer " + jwtService.createRefreshToken(saved1);

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
        String saved2AccessToken = "Bearer " + jwtService.createAccessToken(saved2);
        String saved2refreshToken = "Bearer " + jwtService.createRefreshToken(saved2);

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

    @Operation(summary = "파티 현황 테스트 데이터 생성", description = "파티 현황 테스트를 위한 더미 데이터 생성 API\n\n" +
            "1. 유저 3명을 만든다, 파티 3개를 만든다\n" +
            "=> 유저1 : 파티1,파티2\t\t유저2 : 파티3\t\t유저3 : 파티 없음\n" +
            "2. 파티 신청을 맺는다\n" +
            "=> 유저1은 파티3에 참가요청, 유저2는 파티1에 참가요청, 유저3은 파티1, 파티2에 참가요청\n" +
            "3. 파티를 전부 수락!!\n\n" +
            "예상결과\n" +
            "\n" +
            "=> 유저1\n" +
            "\t만든 파티 -> 파티1, 파티2\n" +
            "\t속한 파티 -> 파티3\n" +
            "=> 유저2\n" +
            "\t만든 파티 -> 파티3\n" +
            "\t속한 파티 -> 파티1\n" +
            "=> 유저3\n" +
            "\t만든 파티 -> 파티 없음\n" +
            "\t속한 파티 -> 파티1, 파티2")
    @PostMapping("/matitting4")
    public ResponseEntity<Map<String, Object>> testPartyStatus() {
        Optional<User> bySocialId_01 = userRepository.findBySocialId("1");
        Optional<User> bySocialId_02 = userRepository.findBySocialId("2");
        Optional<User> bySocialId_03 = userRepository.findBySocialId("3");

        bySocialId_01.ifPresent(user -> userRepository.deleteById(user.getId()));
        bySocialId_02.ifPresent(user -> userRepository.deleteById(user.getId()));
        bySocialId_03.ifPresent(user -> userRepository.deleteById(user.getId()));

        User user1 = User.builder()
                .socialId("1")
                .oauthProvider(OauthProvider.NAVER)
                .email("1@naver.com")
                .nickname("유저1")
                .age(1)
                .imgUrl("유저1.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .socialId("2")
                .oauthProvider(OauthProvider.KAKAO)
                .email("2@kakao.com")
                .nickname("유저2")
                .age(2)
                .imgUrl("유저2.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user3 = User.builder()
                .socialId("3")
                .oauthProvider(OauthProvider.NAVER)
                .email("3@naver.com")
                .nickname("유저3")
                .age(3)
                .imgUrl("유저3.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User u1 = userRepository.save(user1);
        User u2 = userRepository.save(user2);
        User u3 = userRepository.save(user3);

        String u1AccessToken = jwtService.createAccessToken(u1);
        String u2AccessToken = jwtService.createAccessToken(u2);
        String u3AccessToken = jwtService.createAccessToken(u3);

        PartyCreateDto partyCreateDto1 = PartyCreateDto.builder()
                .partyTitle("유저1의 파티")
                .partyContent("방장은 유저1입니다")
                .partyPlaceName("유저1집")
                .partyTime(LocalDateTime.now().plusDays(3))
                .totalParticipant(6)
                .longitude(126.9854393172053)
                .latitude(37.545685580653476)
                .gender(Gender.ALL)
                .category(PartyCategory.KOREAN)
                .age(PartyAge.ALL)
                .menu("제육볶음")
                .thumbnail("유저1 증명사진.jpg")
                .build();

        PartyCreateDto partyCreateDto2 = PartyCreateDto.builder()
                .partyTitle("유저1의 파티")
                .partyContent("방장은 유저1입니다")
                .partyPlaceName("유저1집")
                .partyTime(LocalDateTime.now().plusDays(5))
                .totalParticipant(4)
                .longitude(126.9854393172053)
                .latitude(37.545685580653476)
                .gender(Gender.ALL)
                .category(PartyCategory.KOREAN)
                .age(PartyAge.ALL)
                .menu("돈까스")
                .thumbnail("유저1 증명사진.jpg")
                .build();

        PartyCreateDto partyCreateDto3 = PartyCreateDto.builder()
                .partyTitle("유저2의 파티")
                .partyContent("방장은 유저2입니다")
                .partyPlaceName("유저2집")
                .partyTime(LocalDateTime.now().plusDays(1))
                .longitude(126.9854393172058)
                .latitude(37.545685580653484)
                .gender(Gender.ALL)
                .category(PartyCategory.KOREAN)
                .age(PartyAge.ALL)
                .menu("국밥")
                .thumbnail("유저2 증명사진.jpg")
                .build();

        ResponseCreatePartyDto p1 = partyService.createParty(u1.getId(), partyCreateDto1);
        ResponseCreatePartyDto p2 = partyService.createParty(u1.getId(), partyCreateDto2);
        ResponseCreatePartyDto p3 = partyService.createParty(u2.getId(), partyCreateDto3);

        /**
         * 유저1이 파티3에 신청
         */
        PartyJoinDto partyJoinDto1 = new PartyJoinDto(p3.getPartyId(), PartyJoinStatus.APPLY, "안녕하세요 유저1입니다.");
        PartyDecisionDto partyDecisionDto1 = new PartyDecisionDto(p3.getPartyId(), "유저1", PartyDecision.ACCEPT);
        partyService.joinParty(partyJoinDto1, u1.getId());
        partyService.decideUser(partyDecisionDto1, u2.getId());

        /**
         * 유저2이 파티1에 신청
         */
        PartyJoinDto partyJoinDto2 = new PartyJoinDto(p1.getPartyId(), PartyJoinStatus.APPLY, "안녕하세요 유저2입니다.");
        PartyDecisionDto partyDecisionDto2 = new PartyDecisionDto(p1.getPartyId(), "유저2", PartyDecision.ACCEPT);
        partyService.joinParty(partyJoinDto2, u2.getId());
        partyService.decideUser(partyDecisionDto2, u1.getId());

        /**
         * 유저3이 파티1, 파티2에 신청
         */
        PartyJoinDto partyJoinDto3_1 = new PartyJoinDto(p1.getPartyId(), PartyJoinStatus.APPLY, "안녕하세요 유저3입니다.");
        PartyJoinDto partyJoinDto3_2 = new PartyJoinDto(p2.getPartyId(), PartyJoinStatus.APPLY, "안녕하세요 유저3입니다.");
        PartyDecisionDto partyDecisionDto3_1 = new PartyDecisionDto(p1.getPartyId(), "유저3", PartyDecision.ACCEPT);
        PartyDecisionDto partyDecisionDto3_2 = new PartyDecisionDto(p2.getPartyId(), "유저3", PartyDecision.ACCEPT);
        partyService.joinParty(partyJoinDto3_1, u3.getId());
        partyService.joinParty(partyJoinDto3_2, u3.getId());
        partyService.decideUser(partyDecisionDto3_1, u1.getId());
        partyService.decideUser(partyDecisionDto3_2, u1.getId());

        Map<String, Object> ResponseData = new HashMap<>();
        ResponseData.put("유저1의 ID", u1.getId());
        ResponseData.put("유저1의 access token", u1AccessToken);
        ResponseData.put("유저2의 Id", u2.getId());
        ResponseData.put("유저2의 access token", u2AccessToken);
        ResponseData.put("유저3의 ID", u3.getId());
        ResponseData.put("유저3의 access token", u3AccessToken);

        return ResponseEntity.ok(ResponseData);
    }

    @Operation(summary = "DB 데이터 초기화", description = "모든 데이터를 삭제하는 API 테스트 시에만 사용 예정")
    @DeleteMapping("/matitting")
    public ResponseEntity<?> deleteAll() {
        partyRepository.deleteAll();
        userRepository.deleteAll();
        teamRepository.deleteAll();
        reviewRepository.deleteAll();
        chatRepository.deleteAll();
        chatRoomRepository.deleteAll();
        chatUserRepository.deleteAll();

        return ResponseEntity.ok("success");
    }
}