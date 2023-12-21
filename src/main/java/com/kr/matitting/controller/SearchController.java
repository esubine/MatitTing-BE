package com.kr.matitting.controller;

import com.kr.matitting.dto.*;
import com.kr.matitting.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "파티 검색", description = "파티방을 검색하는 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파티방 검색 리스트 반환", content = @Content(schema = @Schema(implementation = ResponseSearchPageDto.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseSearchPageDto> partySearch(
            @ModelAttribute PartySearchCondDto partySearchCondDto,
            @Schema(description = "불러올 개수", example = "5") @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
            @Schema(description = "마지막 파티 ID", example = "0") @RequestParam(value = "lastPartyId", required = false) Long lastPartyId
    ) {
        ResponseSearchPageDto partyPageTest = searchService.getPartyPage(partySearchCondDto, size, lastPartyId);
        return ResponseEntity.ok().body(partyPageTest);
    }

    @Operation(summary = "인기 검색어", description = "인기 검색어 TOP 10")
    @ApiResponse(responseCode = "200", description = "검색어 TOP 10 성공", content = @Content(schema = @Schema(implementation = String.class)))
    @GetMapping(value = "/rank", produces="application/json;charset=UTF-8")
    public ResponseEntity<List<String>> searchRankList() {
        List<String> responseRankingDtoList = searchService.searchRankList();
        return ResponseEntity.ok().body(responseRankingDtoList);
    }
}
