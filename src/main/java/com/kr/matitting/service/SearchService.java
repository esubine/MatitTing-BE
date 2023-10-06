package com.kr.matitting.service;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.repository.PartyRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PartyRepositoryCustom partyRepositoryCustom;

    public Page<Party> getPartyPage(PartySearchCondDto partySearchCondDto, Pageable pageable) {
        return partyRepositoryCustom.searchPage(partySearchCondDto, pageable);
    }
    public void increaseKeyWordScore(String keyWord) {
        Double score = 0.0;
        try {
            // 검색을하면 해당검색어를 value에 저장하고, score를 1 준다
            redisTemplate.opsForZSet().incrementScore("ranking", keyWord,1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        redisTemplate.opsForZSet().incrementScore("ranking", keyWord, score);

    }
    public List<ResponseRankingDto> SearchRankList() {
        String key = "ranking";
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        // score 기준 1 ~ 10 값을 추출
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 9);
        return typedTuples.stream().map(set -> new ResponseRankingDto(set.getValue(), set.getScore())).collect(Collectors.toList());
    }
}
