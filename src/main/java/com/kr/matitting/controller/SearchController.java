package com.kr.matitting.controller;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping({"/main/search", "/main/search/{page}"})
    public ResponseEntity partySearch(PartySearchCondDto partySearchCondDto,
                                      @PathVariable(name = "page") Optional<Integer> page,
                                      @RequestParam int limit,
                                      @RequestParam Map<String, String> orders) {
        //검색 keyword score increase
        if (partySearchCondDto.getPartyTitle() != null) {
            searchService.increaseKeyWordScore(partySearchCondDto.getPartyTitle());
        }

        //TODO: offset, limit 활용법 제대로 익히기
        PageRequest pageable = PageRequest.of(!page.isPresent() ? 0 : page.get(), limit);
        orders.forEach((orderColumn, orderType) -> {
            pageable.withSort(
                    (orderType == "desc") ? Sort.Direction.DESC : Sort.Direction.ASC, orderColumn);
        });

        searchService.getPartyPage(partySearchCondDto, pageable);

        return null;
    }

    @GetMapping("/api/search/rank")
    public ResponseEntity<List<ResponseRankingDto>> searchRankList() {
        List<ResponseRankingDto> responseRankingDtoList = searchService.SearchRankList();
        return ResponseEntity.ok().body(responseRankingDtoList);
    }
}
