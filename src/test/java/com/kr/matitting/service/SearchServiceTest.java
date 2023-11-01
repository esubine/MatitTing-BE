package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseSearchDto;
import com.kr.matitting.dto.SortDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyRepositoryCustom;
import com.kr.matitting.repository.UserRepository;
import lombok.Data;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class SearchServiceTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SearchService searchService;
    @Autowired
    private PartyService partyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private PartyRepositoryCustom partyRepositoryCustom;

    public static Long partyId;
    public static List<Long> partyId_list = new ArrayList<>();
    public static String userSocialId = "098";
    public static String guestSocialId = "12345";

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
    @Data
    public class SearchRankDto{
        private String keyword;
        private Double score;
        public SearchRankDto(String keyword, Double score) {
            this.keyword = keyword;
            this.score = score;
        }
    }

    @Test
    @DisplayName("인기 검색어")
    void 인기검색어_성공_TOP10() {
        //given
        PartySearchCondDto partySearchCondDto_01 = new PartySearchCondDto("돈까스", null, null, null, 3);
        PartySearchCondDto partySearchCondDto_02 = new PartySearchCondDto("돈까스", null, null, null, 3);
        PartySearchCondDto partySearchCondDto_03 = new PartySearchCondDto("피자", null, null, null, 3);
        PartySearchCondDto partySearchCondDto_04 = new PartySearchCondDto("파스타", null, null, null, 3);
        PageRequest pageable_01 = createPageable(partySearchCondDto_01);
        PageRequest pageable_02 = createPageable(partySearchCondDto_02);
        PageRequest pageable_03 = createPageable(partySearchCondDto_03);
        PageRequest pageable_04 = createPageable(partySearchCondDto_04);

        //when
        searchService.getPartyPage(partySearchCondDto_01, pageable_01);
        searchService.getPartyPage(partySearchCondDto_02, pageable_02);
        searchService.getPartyPage(partySearchCondDto_03, pageable_03);
        searchService.getPartyPage(partySearchCondDto_04, pageable_04);

        String key = "ranking";
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 9);  //score순으로 10개 보여줌
        List<SearchRankDto> searchRankDtoList = typedTuples.stream().map(scoreValue -> new SearchRankDto(scoreValue.getValue(), scoreValue.getScore())).collect(Collectors.toList());

        //then
        assertThat(searchRankDtoList.get(0).keyword).isEqualTo("돈까스");
        assertThat(searchRankDtoList.size()).isEqualTo(3);
    }

    private PageRequest createPageable(PartySearchCondDto partySearchCondDto) {
        SortDto sortDto = partySearchCondDto.sortDto();
        PageRequest pageable = PageRequest.of(0, partySearchCondDto.limit(),
                sortDto.getOrders() == Orders.DESC ? Sort.by(sortDto.getSorts().getKey()).descending() : Sort.by(sortDto.getSorts().getKey()).ascending());
        return pageable;
    }

    @Test
    void 파티방검색_성공_제목() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("전주에서", null, null, null, 10);
        //when
        List<ResponseSearchDto> parties = searchService.getPartyPage(partySearchCondDto, PageRequest.of(0, partySearchCondDto.limit()));

        //then
        assertThat(parties.size()).isEqualTo(4);
    }

    @Test
    void 파티방검색_성공_메뉴() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto(null, "뇨끼", null, null, 10);

        //when
        List<ResponseSearchDto> parties = searchService.getPartyPage(partySearchCondDto, PageRequest.of(0, partySearchCondDto.limit()));

        //then
        assertThat(parties.size()).isEqualTo(3);
    }

    @Test
    void 파티방검색_성공_상태() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto(null, null, PartyStatus.RECRUIT, null, 15);

        //when
        List<ResponseSearchDto> parties = searchService.getPartyPage(partySearchCondDto, PageRequest.of(0, partySearchCondDto.limit()));

        //then
        assertThat(parties.size()).isEqualTo(12);
    }

    @Test
    void 파티방검색_성공_제목_정렬_조회순() {
        //given
        SortDto sortDto = new SortDto(Sorts.HIT, Orders.DESC);

        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("전주", null, null, sortDto, 10);
        
        //when
        PageRequest pageable = createPageable(partySearchCondDto);
        List<ResponseSearchDto> parties = searchService.getPartyPage(partySearchCondDto, pageable);

        //then
        assertThat(parties.size()).isEqualTo(4);
        assertThat(parties.get(0).getHit() >= parties.get(1).getHit()).isTrue();
        assertThat(parties.get(1).getHit() >= parties.get(2).getHit()).isTrue();
        assertThat(parties.get(2).getHit() >= parties.get(3).getHit()).isTrue();
    }

    @Test
    void 파티방검색_성공_제목_정렬_최신순() {
        //given
        SortDto sortDto = new SortDto(Sorts.LATEST, Orders.DESC);
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("수원", null, null, sortDto, 10);

        //when
        PageRequest pageable = createPageable(partySearchCondDto);
        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, pageable);

        //then
        assertThat(parties.getTotalElements()).isEqualTo(4);
        assertThat(parties.getContent().get(0).getPartyTitle()).isEqualTo("수원에서 만나요16");
    }

    @Test
    void 파티방검색_성공_제목_정렬_마감순() {
        //given
        SortDto sortDto = new SortDto(Sorts.LATEST, Orders.ASC);
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("서울", null, null, sortDto, 10);

        //when
        PageRequest pageable = createPageable(partySearchCondDto);
        List<ResponseSearchDto> parties = searchService.getPartyPage(partySearchCondDto, pageable);

        //then
        assertThat(parties.size()).isEqualTo(4);
        assertThat(parties.get(0).getTitle()).isEqualTo("서울에서 만나요0");
    }

    @Test
    void 파티검색_실패_limit_없음() {
        //given
        SortDto sortDto = new SortDto(Sorts.DEADLINE, Orders.ASC);
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("서울", null, null, sortDto, null);

        //when
        assertThrows(NullPointerException.class, () -> createPageable(partySearchCondDto));
        }

    @Test
    void 파티검색_실패_Pageable_없음() {
        //given
        SortDto sortDto = new SortDto(Sorts.DEADLINE, Orders.ASC);
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("서울", null, null, sortDto, null);

        //when
        PageRequest pageRequest = null;

        //then
        assertThrows(NullPointerException.class, () ->searchService.getPartyPage(partySearchCondDto, pageRequest));
    }
}