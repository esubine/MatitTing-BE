package com.kr.matitting.controller;

import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.ResponsePartyDto;
import com.kr.matitting.service.PartyService;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private final PartyService partyService;

    @Operation(summary = "메인 페이지", description = "메인 페이지 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponsePartyDto.class)))})
    })
    @GetMapping("/")
    public ResponseEntity<List<ResponsePartyDto>> getPartyList(
            @ModelAttribute MainPageDto mainPageDto,
            @RequestParam(value = "offset") Integer offset,
            @RequestParam(value = "limit") Integer limit
    ) {
        Pageable pageable = PageRequest.of(offset, limit);
        List<ResponsePartyDto> partyList = partyService.getPartyList(mainPageDto, pageable);
        return ResponseEntity.ok().body(partyList);
    }
}
