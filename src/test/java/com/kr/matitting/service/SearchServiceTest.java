package com.kr.matitting.service;

import com.kr.matitting.constant.*;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.repository.PartyRepository;
import com.kr.matitting.repository.PartyRepositoryCustom;
import com.kr.matitting.repository.UserRepository;
import lombok.Data;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

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

    @BeforeEach
    void clear(){
        redisTemplate.keys("*").stream().forEach(k-> {
            redisTemplate.delete(k);
        });
    }

    @BeforeEach
    public void 데이터생성() {
        User user = User.builder()
                .socialId("30123")
                .socialType(SocialType.KAKAO)
                .email("parksn5029@nate.com")
                .nickname("새싹개발자")
                .age(26)
                .imgUrl("https://www.naver.com")
                .gender(Gender.MALE)
                .role(Role.USER)
                .build();
        userRepository.save(user);

        User guest = User.builder()
                .socialId("12134")
                .socialType(SocialType.KAKAO)
                .email("parkjd5029@gmail.com")
                .nickname("안경잡이개발자")
                .age(26)
                .imgUrl("https://www.google.com")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();
        userRepository.save(guest);

        List<String> title_list = new LinkedList<>();
        title_list.add("아무거나 먹을사람");
        title_list.add("선택해봐요");
        title_list.add("두루와");
        title_list.add("먹는거 좋아하는 사람");
        title_list.add("뭐먹을래?");

        List<String> menu_list = new LinkedList<>();
        menu_list.add("돈까스");
        menu_list.add("피자");
        menu_list.add("파스타");
        menu_list.add("김치찌개");
        menu_list.add("된장찌개");

        for (int i = 1; i <= 20; i++) {
            Party party = Party.builder()
                    .partyTitle(title_list.get(i % 5) + String.valueOf(i))
                    .partyContent(String.valueOf(i)+"번째 Content")
                    .address("전주")
                    .status((i%2 == 0) ? PartyStatus.RECRUIT : PartyStatus.FINISH)
                    .deadline(LocalDateTime.of(2023, 10, 12, 15, 23, i))
                    .partyTime(LocalDateTime.of(2024, 10, 12, 15, 23, i))
                    .totalParticipant(4)
                    .participantCount(1)
                    .gender(Gender.ALL)
                    .age(PartyAge.AGE2030)
                    .hit(i)
                    .thumbnail("메뉴사진.jpg")
                    .user(user)
                    .menu(menu_list.get(i%5))
                    .build();
            partyRepository.save(party);
        }
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
    void 인기검색어() {
        searchService.increaseKeyWordScore("김치찌개");
        searchService.increaseKeyWordScore("김치찌개");
        searchService.increaseKeyWordScore("김치찌개");
        searchService.increaseKeyWordScore("김치찌개");

        searchService.increaseKeyWordScore("돈까스");
        searchService.increaseKeyWordScore("돈까스");
        searchService.increaseKeyWordScore("돈까스");

        searchService.increaseKeyWordScore("부대찌개");
        searchService.increaseKeyWordScore("부대찌개");

        searchService.increaseKeyWordScore("피자");
        searchService.increaseKeyWordScore("ㅍㅣ자");

        String key = "ranking";
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 4);  //score순으로 10개 보여줌
        List<SearchRankDto> searchRankDtoList = typedTuples.stream().map(scoreValue -> new SearchRankDto(scoreValue.getValue(), scoreValue.getScore())).collect(Collectors.toList());

        int i = 1;
        for (SearchRankDto searchRankDto : searchRankDtoList) {
            System.out.println(i+"번째 키워드:"+searchRankDto.keyword+ "스코어:" + searchRankDto.score);
            i++;
        }
    }

    @Test
    void 파티방검색_제목() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("사람", null, null, null, 10);
        //when
        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, PageRequest.of(0, partySearchCondDto.limit()));

        //then
        assertThat(parties.getTotalElements()).isEqualTo(8);
    }

    @Test
    void 파티방검색_메뉴() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto(null, "김치찌개", null, null, 10);

        //when
        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, PageRequest.of(0, partySearchCondDto.limit()));

        //then
        assertThat(parties.getTotalElements()).isEqualTo(4);
    }

    @Test
    void 파티방검색_상태() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto(null, null, PartyStatus.RECRUIT, null, 10);

        //when
        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, PageRequest.of(0, partySearchCondDto.limit()));

        //then
        assertThat(parties.getTotalElements()).isEqualTo(10);
    }

    @Test
    void 파티방검색_제목_정렬_조회순() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("사람", null, null, null, 10);

        Map<String, String> orders = new HashMap<>();
        orders.put("column", "hit");
        orders.put("type", "desc");

        //when
        PageRequest pageable = PageRequest.of(0, partySearchCondDto.limit(),
                orders.get("type") == "desc" ? Sort.by(orders.get("column")).descending() : Sort.by(orders.get("column")).ascending());

        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, pageable);

        //then
        assertThat(parties.getTotalElements()).isEqualTo(8);
        assertThat(parties.getContent().get(0).getHit()).isEqualTo(20);
    }

    @Test
    void 파티방검색_제목_정렬_최신순() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("사람", null, null, null, 10);

        Map<String, String> orders = new HashMap<>();
        orders.put("column", "latest");
        orders.put("type", "desc");

        //when
        PageRequest pageable = PageRequest.of(0, partySearchCondDto.limit(),
                orders.get("type") == "desc" ? Sort.by(orders.get("column")).descending() : Sort.by(orders.get("column")).ascending());

        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, pageable);

        //then
        assertThat(parties.getTotalElements()).isEqualTo(8);
        assertThat(parties.getContent().get(0).getPartyTitle()).isEqualTo("아무거나 먹을사람20");
    }

    @Test
    void 파티방검색_제목_정렬_마감순() {
        //given
        PartySearchCondDto partySearchCondDto = new PartySearchCondDto("사람", null, null, null, 10);

        Map<String, String> orders = new HashMap<>();
        orders.put("column", "deadline");
        orders.put("type", "asc");

        //when
        PageRequest pageable = PageRequest.of(0, partySearchCondDto.limit(),
                orders.get("type") == "desc" ? Sort.by(orders.get("column")).descending() : Sort.by(orders.get("column")).ascending());

        Page<Party> parties = partyRepositoryCustom.searchPage(partySearchCondDto, pageable);

        //then
        assertThat(parties.getTotalElements()).isEqualTo(8);
        assertThat(parties.getContent().get(0).getPartyTitle()).isEqualTo("먹는거 좋아하는 사람3");
    }

}