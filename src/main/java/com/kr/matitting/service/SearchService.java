package com.kr.matitting.service;

import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.repository.PartyRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class SearchService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PartyRepositoryCustom partyRepositoryCustom;

    public List<PartyCreateDto> getPartyPage(PartySearchCondDto partySearchCondDto, Pageable pageable) {
        if (!(partySearchCondDto.title() == null)) {
            increaseKeyWordScore(partySearchCondDto.title());
        }
        if (!(partySearchCondDto.menu() == null)) {
            increaseKeyWordScore(partySearchCondDto.menu());
        }
        List<PartyCreateDto> partyList = partyRepositoryCustom.searchPage(partySearchCondDto, pageable).stream().map(party -> PartyCreateDto.toDto(party)).toList();
        return partyList;
    }
    public void increaseKeyWordScore(String keyWord) {
        log.info("=== increaseKeyWordScore() start ===");

        Double score = 0.0;
        try {
            redisTemplate.opsForZSet().incrementScore("ranking", keyWord,1);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        redisTemplate.opsForZSet().incrementScore("ranking", keyWord, score);

    }
    public List<ResponseRankingDto> searchRankList() {
        log.info("=== searchRankList() start ===");

        String key = "ranking";
        ZSetOperations<String, String> ZSetOperations = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> typedTuples = ZSetOperations.reverseRangeWithScores(key, 0, 9);
        return typedTuples.stream().map(set -> new ResponseRankingDto(set.getValue(), set.getScore())).collect(Collectors.toList());
    }
}
