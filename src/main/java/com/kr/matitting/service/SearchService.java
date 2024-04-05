package com.kr.matitting.service;

import com.kr.matitting.constant.Orders;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.repository.PartyRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
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

    public ResponseSearchPageDto getPartyPage(Pageable pageable, PartySearchCondDto partySearchCondDto) {
        if (partySearchCondDto.keyword() == null) return new ResponseSearchPageDto();
        else increaseKeyWordScore(partySearchCondDto.keyword());


        PageRequest pageRequest = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                partySearchCondDto.sortDto().getOrders().equals(Orders.DESC) ? Sort.by(partySearchCondDto.sortDto().getSorts().getKey()).descending() : Sort.by(partySearchCondDto.sortDto().getSorts().getKey()).ascending()
        );
        Page<Party> parties = partyRepositoryCustom.searchPage(pageRequest, partySearchCondDto);
        return new ResponseSearchPageDto(parties.stream().map(ResponsePartyDto::toDto).collect(Collectors.toList()), new ResponsePageInfoDto(pageable.getPageNumber(), parties.hasNext()));
    }

    public void increaseKeyWordScore(String keyWord) {
        log.info("=== increaseKeyWordScore() start ===");

        int score = 0;
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
        return typedTuples != null ? typedTuples.stream().map(set -> new ResponseRankingDto(set.getValue())).toList() : null;
    }

    private Long getLastPartyId(List<ResponsePartyDto> responsePartyList){
        return responsePartyList.isEmpty() ? null : responsePartyList.get(responsePartyList.size() - 1).getPartyId();
    }
}
