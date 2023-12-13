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
            @ModelAttribute MainPageDto mainPageDto
    ) {
        Pageable pageable = PageRequest.of(0, mainPageDto.size());
        ResponseMainPageDto mainPageList = mainService.getPartyList(mainPageDto, pageable);
        return ResponseEntity.ok().body(mainPageList);
    }
}
