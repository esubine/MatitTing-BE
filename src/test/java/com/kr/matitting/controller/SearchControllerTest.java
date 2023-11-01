package com.kr.matitting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kr.matitting.constant.*;
import com.kr.matitting.dto.SortDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.UserRepository;
import com.kr.matitting.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    public static Long partyId;
    public static List<Long> partyId_list = new ArrayList<>();
    static String accessToken;
    static String refreshToken;

    @BeforeEach
    void clear(){
        redisTemplate.keys("*").stream().forEach(k-> {
            redisTemplate.delete(k);
        });
    }
    @BeforeEach
    public void 데이터생성() {
        User user = User.builder()
                .socialId("12345")
                .socialType(SocialType.KAKAO)
                .email("test@naver.com")
                .nickname("새싹개발자")
                .age(26)
                .imgUrl("증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        userRepository.save(user);

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

        User user1 = User.builder()
                .socialId("098")
                .socialType(SocialType.KAKAO)
                .email("user1@naver.com")
                .nickname("User1")
                .age(26)
                .imgUrl("첫째 증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();

        User user2 = User.builder()
                .socialId("456")
                .socialType(SocialType.NAVER)
                .email("user2@naver.com")
                .nickname("User2")
                .age(20)
                .imgUrl("둘째 증명사진.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();

        User user3 = User.builder()
                .socialId("836")
                .socialType(SocialType.KAKAO)
                .email("user3@naver.com")
                .nickname("User3")
                .age(36)
                .imgUrl("셋째 증명사진.jpg")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);
        users.add(user3);

        List<String> title_list = new ArrayList<>();
        title_list.add("서울에서 만나요");
        title_list.add("수원에서 만나요");
        title_list.add("전주에서 만나요");
        title_list.add("광주에서 만나요");
        title_list.add("전국에서 만나요");

        List<PartyStatus> status_list = new ArrayList<>();
        status_list.add(PartyStatus.RECRUIT);
        status_list.add(PartyStatus.RECRUIT);
        status_list.add(PartyStatus.FINISH);
        status_list.add(PartyStatus.RECRUIT);
        status_list.add(PartyStatus.FINISH);
        status_list.add(PartyStatus.FINISH);
        status_list.add(PartyStatus.RECRUIT);

        List<String> menu_list = new ArrayList<>();
        menu_list.add("돈까스");
        menu_list.add("피자");
        menu_list.add("치킨");
        menu_list.add("짜장면");
        menu_list.add("뇨끼");
        menu_list.add("김치볶음밥");
        menu_list.add("삼겹살");

        List<PartyCategory> category_list = new ArrayList<>();
        category_list.add(PartyCategory.JAPANESE);
        category_list.add(PartyCategory.WESTERN);
        category_list.add(PartyCategory.KOREAN);
        category_list.add(PartyCategory.CHINESE);
        category_list.add(PartyCategory.WESTERN);
        category_list.add(PartyCategory.KOREAN);
        category_list.add(PartyCategory.KOREAN);

        List<LocalDateTime> deadline_list = new ArrayList<>();
        deadline_list.add(LocalDateTime.now().plusDays(1));
        deadline_list.add(LocalDateTime.now().plusDays(2));
        deadline_list.add(LocalDateTime.now().plusDays(3));

        List<LocalDateTime> partyTime_list = new ArrayList<>();
        partyTime_list.add(LocalDateTime.now().plusDays(3));
        partyTime_list.add(LocalDateTime.now().plusDays(4));
        partyTime_list.add(LocalDateTime.now().plusDays(5));

        List<Integer> total_list = new ArrayList<>();
        total_list.add(3);
        total_list.add(4);
        total_list.add(5);
        total_list.add(6);

        List<Integer> current_list = new ArrayList<>();
        current_list.add(1);
        current_list.add(2);
        current_list.add(3);

        List<Gender> gender = new ArrayList<>();
        gender.add(Gender.ALL);
        gender.add(Gender.FEMALE);
        gender.add(Gender.MALE);

        List<PartyAge> age = new ArrayList<>();
        age.add(PartyAge.ALL);
        age.add(PartyAge.AGE2030);
        age.add(PartyAge.AGE3040);
        age.add(PartyAge.AGE40);

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            Party newParty = Party.builder()
                    .partyTitle(title_list.get(i % 5)+String.valueOf(i))
                    .partyContent(random_word(random, 30))
                    .address(random_word(random, 10))
                    .latitude(Math.random())
                    .longitude(Math.random())
                    .status(status_list.get(i % 7))
                    .deadline(deadline_list.get(i%3).plusMinutes(i))
                    .partyTime(partyTime_list.get(i%3))
                    .totalParticipant(total_list.get(i%4))
                    .participantCount(current_list.get(i%3))
                    .gender(gender.get(i%3))
                    .age(age.get(i%4))
                    .hit((int)(Math.random()*100))
                    .menu(menu_list.get(i % 7))
                    .category(category_list.get(i%7))
                    .user(users.get(i%3))
                    .build();
            Party saveParty = partyRepository.save(newParty);
            partyId_list.add(saveParty.getId());
        }
    }
    @BeforeEach
    void 토큰발급() {
        //로그인 후 토큰 발급!!
        UserSignUpDto userSignUpDto = new UserSignUpDto("132321321321", SocialType.KAKAO, "signUp@naver.com", "안경잡이개발자", 26, "증명사진.jpg", Gender.MALE);
        User user = userService.signUp(userSignUpDto);
        accessToken = "BEARER "+jwtService.createAccessToken(user);
        refreshToken = "BEARER "+jwtService.createRefreshToken(user);
        jwtService.updateRefreshToken(user.getSocialId(), refreshToken);
    }

    @AfterEach
    void clean() {
        partyId_list.clear();
    }
    private String random_word(Random random, int length) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = length;
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Test
    void 인기검색어_성공_TOP10() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/rank")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }
    @Test
    void 파티방검색_성공_제목() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "전주에서")
                        .param("limit", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티방검색_성공_메뉴() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("menu", "뇨끼")
                        .param("limit", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티방검색_성공_상태() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", String.valueOf(PartyStatus.RECRUIT))
                        .param("limit", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티검색_성공_제목_상태() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search")
                        .with(csrf())
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "전주에서")
                        .param("status", String.valueOf(PartyStatus.RECRUIT))
                        .param("limit", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티방검색_제목_정렬_조회순() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "전주에서")
                        .param("sorts", String.valueOf(Sorts.HIT))
                        .param("orders", String.valueOf(Orders.DESC))
                        .param("limit", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티방검색_제목_정렬_최신순() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "전주에서")
                        .param("sorts", String.valueOf(Sorts.LATEST))
                        .param("orders", String.valueOf(Orders.DESC))
                        .param("limit", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티방검색_제목_정렬_마감순() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("title", "전주에서")
                        .param("sorts", String.valueOf(Sorts.DEADLINE))
                        .param("orders", String.valueOf(Orders.ASC))
                        .param("limit", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티검색_성공_제목_상태_조회순() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", String.valueOf(PartyStatus.RECRUIT))
                        .param("sorts", String.valueOf(Sorts.HIT))
                        .param("orders", String.valueOf(Orders.DESC))
                        .param("limit", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티검색_성공_제목_상태_최신순() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", String.valueOf(PartyStatus.RECRUIT))
                        .param("sorts", String.valueOf(Sorts.LATEST))
                        .param("orders", String.valueOf(Orders.DESC))
                        .param("limit", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티검색_성공_제목_상태_마감순() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", String.valueOf(PartyStatus.RECRUIT))
                        .param("sorts", String.valueOf(Sorts.DEADLINE))
                        .param("orders", String.valueOf(Orders.DESC))
                        .param("limit", "2"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(print());
    }

    @Test
    void 파티검색_실패_limit_없음() throws Exception {
        //when, then
        mockMvc.perform(get("/api/search/1")
                        .header(jwtService.getAccessHeader(), accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("status", String.valueOf(PartyStatus.RECRUIT))
                        .param("sorts", String.valueOf(Sorts.DEADLINE))
                        .param("orders", String.valueOf(Orders.DESC)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(print());
    }
}