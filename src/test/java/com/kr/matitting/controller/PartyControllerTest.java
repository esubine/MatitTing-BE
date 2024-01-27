package com.kr.matitting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.kr.matitting.constant.*;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartyJoinDto;
import com.kr.matitting.dto.PartyUpdateDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.service.PartyService;
import com.kr.matitting.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class PartyControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PartyService partyService;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    static String accessToken;
    static String refreshToken;
    static User user;
    public static Long partyId;

//    @BeforeEach
//    void 토큰발급() {
//        //로그인 후 토큰 발급!!
//        UserSignUpDto userSignUpDto = new UserSignUpDto("12345", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
//        user = userService.signUp(userSignUpDto);
//        accessToken = "BEARER "+jwtService.createAccessToken(user);
//        refreshToken = "BEARER "+jwtService.createRefreshToken(user);
//        jwtService.updateRefreshToken(user.getSocialId(), refreshToken);
//    }
    @BeforeEach
    public void 파티생성() {
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
    }

    @BeforeEach
    void clear(){
        redisTemplate.keys("*").stream().forEach(k-> {
            redisTemplate.delete(k);
        });
    }
    @Test
    void 파티_업데이트_성공_모두() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                LocalDateTime.now().plusDays(5)));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    //TODO: 수정..
    @Test
    void 파티_업데이트_성공_제목() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
                "저랑 놀사람!",
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
                null));
        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    void 파티_업데이트_성공_소개글() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                null));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티_업데이트_성공_메뉴() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                null));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티_업데이트_성공_위경도() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                null));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티_업데이트_성공_마감시간() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                null));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티_업데이트_성공_파티시간() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                LocalDateTime.now().plusDays(5)));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티_업데이트_실패_ID_없음() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                null));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
    @Test
    void 파티_업데이트_실패_마감시간_잘못됨() throws Exception {
        //given
        String partyUpdateDto = objectMapper.writeValueAsString(new PartyUpdateDto(
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
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(3)));

        //when, then
        mockMvc.perform(patch("/api/party")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyUpdateDto))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 파티조회_성공() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+partyId)
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티조회_실패_ID_없음() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+"3312321321")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 파티삭제_성공() throws Exception {
        //when, then
        mockMvc.perform(delete("/api/party/"+partyId)
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티삭제_실패_ID_없음() throws Exception {
        //when, then
        mockMvc.perform(delete("/api/party/"+"132312321")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 파티신청_성공() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        String newAccessToken = jwtService.createAccessToken(newUser);
        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT));

        //when, then
        mockMvc.perform(post("/api/party/participation")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), "BEARER "+newAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티신청_실패_방장Id가_없을때() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        String newAccessToken = jwtService.createAccessToken(newUser);
        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(partyId, 213321321L, PartyJoinStatus.WAIT));

        //when, then
        mockMvc.perform(post("/api/party/participation")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), "BEARER "+newAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 파티신청_실패_파티Id가_없을때() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        String newAccessToken = jwtService.createAccessToken(newUser);
        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(123312L, user.getId(), PartyJoinStatus.WAIT));

        //when, then
        mockMvc.perform(post("/api/party/participation")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), "BEARER "+newAccessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 파티요청_수락_성공() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        partyService.joinParty(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT), newUser);

        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.ACCEPT));

        //when, then
        mockMvc.perform(post("/api/party/decision")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티요청_수락_실패_상태대기() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        partyService.joinParty(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT), newUser);

        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT));

        //when, then
        mockMvc.perform(post("/api/party/decision")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 파티요청_수락_실패_파티없음() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        partyService.joinParty(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT), newUser);

        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(32113L, user.getId(), PartyJoinStatus.ACCEPT));

        //when, then
        mockMvc.perform(post("/api/party/decision")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 파티요청_수락_실패_유저없음() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        partyService.joinParty(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT), newUser);

        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(partyId,213321L, PartyJoinStatus.ACCEPT));

        //when, then
        mockMvc.perform(post("/api/party/decision")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void 파티요청_거절_성공() throws Exception {
        //given
        UserSignUpDto userSignUpDto = new UserSignUpDto("321321321", OauthProvider.KAKAO, "test@naver.com", "새싹개발자", 26, "증명사진.jpg", Gender.MALE);
        User newUser = userService.signUp(userSignUpDto);
        partyService.joinParty(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.WAIT), newUser);

        String partyJoinDto = objectMapper.writeValueAsString(new PartyJoinDto(partyId, user.getId(), PartyJoinStatus.REFUSE));

        //when, then
        mockMvc.perform(post("/api/party/decision")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partyJoinDto))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티현황_성공_방장_모집중() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+String.valueOf(user.getId())+"/party-status")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", String.valueOf(Role.HOST)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티현황_성공_지원자_모집중() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+String.valueOf(user.getId())+"/party-status")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", String.valueOf(Role.VOLUNTEER)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티현황_성공_마감() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+String.valueOf(user.getId())+"/party-status")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", String.valueOf(Role.USER)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void 파티현황_실패_ID_Null() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+""+"/party-status")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("role", String.valueOf(Role.USER)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    void 파티현황_실패_Role_Null() throws Exception {
        //when, then
        mockMvc.perform(get("/api/party/"+String.valueOf(user.getId())+"/party-status")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    public void 파티_생성_성공() throws Exception {
        PartyCreateDto partyCreateDto = createPartyCreateDto();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        String requestBody = objectMapper.writeValueAsString(partyCreateDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/party")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.partyId").exists());
    }
    private PartyCreateDto createPartyCreateDto() {
        PartyCreateDto request = new PartyCreateDto();
        request.setUserId(1L);
        request.setTitle("테스트 파티 생성 DTO");
        request.setContent("파티 생성 테스트");
        request.setLatitude(37.566828706631135);
        request.setLongitude(126.978646598009);
        request.setPartyTime(LocalDateTime.now());
        request.setTotalParticipant(5);
        request.setGender(Gender.ALL);
        request.setAge(PartyAge.AGE2030);
        request.setMenu("TEST");
        request.setCategory(PartyCategory.WESTERN);
        return request;
    }

}