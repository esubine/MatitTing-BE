package com.kr.matitting.controller;

import com.kr.matitting.constant.PartyStatus;
import com.kr.matitting.constant.Sorts;
import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.ResponseMainPageDto;
import com.kr.matitting.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private final MainService mainService;

    @Operation(summary = "메인 페이지", description = "메인 페이지 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMainPageDto.class)))})
    })
    @GetMapping
    public ResponseEntity<ResponseMainPageDto> getPartyList(
            @ModelAttribute MainPageDto mainPageDto,
            @Schema(description = "파티상태 - 1. 입력하지 않는 경우(default): 모집 중인 파티만 조회 / 2. FINISH 입력 시: 모든 파티글 조회", nullable = true, example = "FINISH")
            @RequestParam(value = "partyStatus", required = false, defaultValue = "FINISH") PartyStatus partyStatus,
            @Schema(description = "조회할 갯수", nullable = true, example = "5")
            @RequestParam(value = "size", required = false, defaultValue = "5") int size,
            @Schema(description = "마지막으로 조회한 파티 ID", nullable = true, example = "0")
            @RequestParam(value = "lastPartyId", required = false) Long lastPartyId,
            @Schema(description = "정렬기준 - 1. 입력하지 않는 경우(default): 5km 반경의 파티글 중 유저와 가까운순 / 2. LATEST 입력 시: 5km 반경의 파티글 중 최신순 정렬", nullable = true, example = "LATEST")
            @RequestParam(value = "sort", required = false) Sorts sort
    ) {
        Pageable pageable = PageRequest.of(0, size);
        ResponseMainPageDto mainPageList = mainService.getPartyList(mainPageDto, partyStatus, pageable, lastPartyId, sort);
        return ResponseEntity.ok().body(mainPageList);
    }
}
