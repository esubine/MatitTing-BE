package com.kr.matitting.controller;

import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping({"/", "/{page}"})
    public ResponseEntity<List<PartyCreateDto>> partySearch(@ModelAttribute @Valid PartySearchCondDto partySearchCondDto,
                                                            @PathVariable(name = "page") Optional<Integer> page) {
        Map<String, String> orders = partySearchCondDto.checkOrder();

        PageRequest pageable = PageRequest.of(!page.isPresent() ? 0 : page.get(), partySearchCondDto.limit(),
                orders.get("type") == "desc" ? Sort.by(orders.get("column")).descending() : Sort.by(orders.get("column")).ascending());
        List<PartyCreateDto> partyPage = searchService.getPartyPage(partySearchCondDto, pageable);
        return ResponseEntity.ok().body(partyPage);
    }

    @GetMapping("/rank")
    public ResponseEntity<List<ResponseRankingDto>> searchRankList() {
        List<ResponseRankingDto> responseRankingDtoList = searchService.searchRankList();
        return ResponseEntity.ok().body(responseRankingDtoList);
    }
}
