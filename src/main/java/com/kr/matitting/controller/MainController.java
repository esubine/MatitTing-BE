package com.kr.matitting.controller;

import com.kr.matitting.dto.MainPageDto;
import com.kr.matitting.dto.ResponseMainPageDto;
import com.kr.matitting.service.MainService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/main")
public class MainController {
    private final MainService mainService;

    @Operation(summary = "메인 페이지", description = "메인 페이지 API 입니다. \n\n" +
                                                    "[로직설명] \n\n" +
                                                    "1. 메인 페이지 조회에 필요한 값들을 request로 입력받습니다. \n\n" +
                                                    "2. request에 따라 위도, 경도값(사용자 위치) 기준 직선거리 5km 이내 파티들이 조회됩니다. \n\n" +
                                                    "※ mainPageDto request의 partyStatus, sort 입력값에 따라 정렬, 조회가 달라집니다. (mainPageDto 설명 참고) \n\n" +
                                                    "- 조회 default: 모집 중인 파티만 조회 \n\n" +
                                                    "- 정렬 default: 5km 반경의 파티글 중 유저와 가까운순으로 정렬 \n\n" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {
                            @Content(array = @ArraySchema(schema = @Schema(implementation = ResponseMainPageDto.class)))})
    })
    @GetMapping
    public ResponseEntity<ResponseMainPageDto> getPartyList(
            @PageableDefault Pageable pageable,
            @Valid @ModelAttribute MainPageDto mainPageDto
    ) {
        ResponseMainPageDto mainPageList = mainService.getPartyList(mainPageDto, pageable);
        return ResponseEntity.ok().body(mainPageList);
    }
}
