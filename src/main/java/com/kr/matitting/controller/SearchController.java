package com.kr.matitting.controller;

import com.kr.matitting.dto.PartySearchCondDto;
import com.kr.matitting.dto.ResponseRankingDto;
import com.kr.matitting.dto.ResponseSearchDto;
import com.kr.matitting.dto.ResponseSearchPageDto;
import com.kr.matitting.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

//    @Operation(summary = "파티 검색", description = "파티 검색 API \n\n" +
//                                                "파티 검색 시 해당 Text가 포함된 파티방 정보를 불러온다. => 포함되는 속성 값 : 제목, 메뉴, 파티 소개글, 주소 \n\n \n\n" +
//                                                "로직 설명 \n\n" +
//                                                "1. 사용자가 검색창에 keyword를 입력하면 이를 request로 받아서 값을 검사한다 => 값이 null이면 파티방을 return하지 않고, null이 아니면 인기 검색어에 count +1이 진행된다. \n\n" +
//                                                "2. 입력한 keyword 값을 포함하고 있는 파티방을 가져오고 lastPartyId를 검사하여 마지막으로 받은 파티방 정보 다음 값부터의 N개, lastPartyId, hasNext(검색해서 나온 정보들 중 마지막 값인지 아닌지를 return)를 response 한다."
//    )
//    @ApiResponse(responseCode = "200", description = "파티 검색 성공", content = @Content(schema = @Schema(implementation = ResponseSearchDto.class)))
//    @GetMapping
//    public ResponseEntity<ResponseSearchPageDto> partySearch(
//            @ModelAttribute PartySearchCondDto partySearchCondDto,
//            @Schema(description = "불러올 개수", example = "5") @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
//            @Schema(description = "마지막 파티 ID", example = "0")@RequestParam(value = "lastPartyId", required = false) Long lastPartyId
//    ) {
//        ResponseSearchPageDto partyPageTest = searchService.getPartyPage(partySearchCondDto, size, lastPartyId);
//        return ResponseEntity.ok().body(partyPageTest);
//    }

    @Operation(summary = "파티 검색", description = "파티 검색 API \n\n" +
            "파티 검색 시 해당 Text가 포함된 파티방 정보를 불러온다. => 포함되는 속성 값 : 제목, 메뉴, 파티 소개글, 주소\n\n" +
            "Request(query parameter) : page(int), size(int) \n\n" +
            "Request(form-data) : keyword(string), status(string), sortDto.sorts(string), sortDto.orders(string)"
    )
    @ApiResponse(responseCode = "200", description = "파티 검색 성공", content = @Content(schema = @Schema(implementation = ResponseSearchDto.class)))
    @GetMapping
    public ResponseEntity<ResponseSearchPageDto> partySearch(@PageableDefault Pageable pageable, PartySearchCondDto partySearchCondDto) {
        return ResponseEntity.ok().body(searchService.getPartyPage(pageable, partySearchCondDto));
    }

    @Operation(summary = "인기 검색어", description = "인기 검색어 TOP 10 API \n\n" +
                                                    "사용자들이 가장 많이 검색한 검색어 10개를 불러오는 API \n\n \n\n" +
                                                    "로직 설명 \n\n" +
                                                    "1. redis에서 사용자들이 가장 많이 검색한 검색어 10개를 가져와서 response 한다. \n\n" +
                                                    "※ redis에서 지원하는 자료형들 중 Z Set를 사용하는데, 이는 최대 값 정렬을 자동으로 지원하여 keyword:score 구조를 내림차순으로 관리해준다.")
    @ApiResponse(responseCode = "200", description = "인기 검색어 조회 성공", content = @Content(schema = @Schema(implementation = ResponseRankingDto.class)))
    @GetMapping(value = "/rank", produces="application/json;charset=UTF-8")
    public ResponseEntity<List<ResponseRankingDto>> searchRankList() {
        List<ResponseRankingDto> responseRankingDtoList = searchService.searchRankList();
        return ResponseEntity.ok().body(responseRankingDtoList);
    }
}
