package com.kr.matitting.controller;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.entity.Party;
import com.kr.matitting.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping({"/api/search", "/api/search/{page}"})
    public ResponseEntity partySearch(PartySearchCondDto partySearchCondDto,
                                      @PathVariable(name = "page") Optional<Integer> page) {

        PageRequest pageable = PageRequest.of(!page.isPresent() ? 0 : page.get(), partySearchCondDto.getLimit(),
                partySearchCondDto.getOrders().get("type") == "desc" ? Sort.by(partySearchCondDto.getOrders().get("column")).descending() : Sort.by(partySearchCondDto.getOrders().get("column")).ascending());
        Page<Party> partyPage = searchService.getPartyPage(partySearchCondDto, pageable);
        return ResponseEntity.ok().body(partyPage);
    }

    @GetMapping("/api/search/rank")
    public ResponseEntity<List<ResponseRankingDto>> searchRankList() {
        List<ResponseRankingDto> responseRankingDtoList = searchService.searchRankList();
        return ResponseEntity.ok().body(responseRankingDtoList);
    }
}
