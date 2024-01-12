package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.*;
import com.kr.matitting.entity.Party;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.Map.MapExceptionType;
import com.kr.matitting.exception.party.PartyExceptionType;
import com.kr.matitting.exception.partyjoin.PartyJoinExceptionType;
import com.kr.matitting.exception.team.TeamExceptionType;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.service.PartyService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/party")
public class PartyController {
    private final PartyService partyService;
    private final UserService userService;

    // 파티 모집 글 생성
    @Operation(summary = "파티 글 생성", description = "파티 글 생성 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schemaProperties = {
                            @SchemaProperty(name = "partyId", schema = @Schema(type = "long", description = "파티 아이디"))})),
            @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class))),
            @ApiResponse(responseCode = "1300", description = "카카오 맵 Authorization이 실패했습니다.", content = @Content(schema = @Schema(implementation = MapExceptionType.class))),
            @ApiResponse(responseCode = "1301", description = "카카오 맵에서 데이터를 받아오지 못했습니다.", content = @Content(schema = @Schema(implementation = MapExceptionType.class))),
            @ApiResponse(responseCode = "1302", description = "카카오 맵 서버 오류입니다.", content = @Content(schema = @Schema(implementation = MapExceptionType.class)))
    })

    @PostMapping
    public ResponseEntity<Map<String, Long>> createParty(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PartyCreateDto request
    ) {
        Map<String, Long> partyId = partyService.createParty(user, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(partyId);
    }

    @Operation(summary = "파티 업데이트", description = "파티 정보 업데이트 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "800", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyExceptionType.class)))
    })
    @PatchMapping("{partyId}")
    public ResponseEntity<String> updateParty(@RequestBody PartyUpdateDto partyUpdateDto, @PathVariable Long partyId) {
        partyService.partyUpdate(partyUpdateDto, partyId);
        return ResponseEntity.ok().body("Success Party update");
    }

    @Operation(summary = "파티 세부정보", description = "파티의 세부정로 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파티 세부정보 불러오기 성공", content = @Content(schema = @Schema(implementation = ResponsePartyDto.class))),
            @ApiResponse(responseCode = "800", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyExceptionType.class)))
    })
    @GetMapping("/{partyId}")
    public ResponseEntity<ResponsePartyDetailDto> partyDetail(@AuthenticationPrincipal User user, @PathVariable Long partyId) {
        ResponsePartyDetailDto partyInfo = partyService.getPartyInfo(user, partyId);
        return ResponseEntity.ok().body(partyInfo);
    }

    @Operation(summary = "파티 삭제", description = "파티를 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파티 삭제 성공"),
            @ApiResponse(responseCode = "800", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyExceptionType.class)))
})
    @DeleteMapping("/{partyId}")
    public ResponseEntity<String> partyDelete(@AuthenticationPrincipal User user, @PathVariable Long partyId) {
        partyService.deleteParty(user, partyId);
        return ResponseEntity.ok().body("Success Party Delete");
    }

    @Operation(summary = "파티 참가 신청", description = "사용자가 파티를 참가하겠다고 신청하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가 신청 성공"),
            @ApiResponse(responseCode = "700", description = "참가할 파티를 찾지 못했습니다.", content = @Content(schema = @Schema(implementation = PartyJoinExceptionType.class)))
    })
    @PostMapping("/participation")
    public ResponseEntity<Long> JoinParty(@RequestBody @Valid PartyJoinDto partyJoinDto, @AuthenticationPrincipal User user) {
        Long joinPartyId = partyService.joinParty(partyJoinDto, user);
        if (joinPartyId == null) {
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(joinPartyId);
    }

    @Operation(summary = "파티 참가 수락/거절", description = "방장이 참여신청에 대한 수락/거절을 결정하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수락/거절 결정 성공"),
            @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class))),
            @ApiResponse(responseCode = "700", description = "참가할 파티를 찾지 못했습니다.", content = @Content(schema = @Schema(implementation = PartyJoinExceptionType.class))),
            @ApiResponse(responseCode = "702", description = "파티 참가 상태가 잘못되었습니다.", content = @Content(schema = @Schema(implementation = PartyJoinExceptionType.class))),
            @ApiResponse(responseCode = "800", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyExceptionType.class)))
    })
    @PostMapping("/decision")
    public ResponseEntity<String> AcceptRefuseParty(@RequestBody @Valid PartyDecisionDto partyDecisionDto, @AuthenticationPrincipal User user) {
        String result = partyService.decideUser(partyDecisionDto, user);
        return ResponseEntity.ok().body(result);
    }

    @Operation(summary = "파티 현황", description = "내 파티 현황을 불러오는 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파티 현황 불러오기 성공", content = @Content(schema = @Schema(implementation = ResponsePartyDto.class))),
            @ApiResponse(responseCode = "1000", description = "파티 팀 정보가 없습니다.", content = @Content(schema = @Schema(implementation = TeamExceptionType.class)))
    })
    @GetMapping("/party-status")
    public ResponseEntity<List<ResponsePartyDto>> myPartyList(@AuthenticationPrincipal User user,@NotNull @RequestParam Role role) {
        List<ResponsePartyDto> myPartyList = userService.getMyPartyList(user, role);
        return ResponseEntity.ok().body(myPartyList);
    }

    @Operation(summary = "파티 신청 현황", description = "파티 신청 현황을 불러오는 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파티 신청 현황 불러오기 성공", content = @Content(schema = @Schema(implementation = ResponsePartyDto.class))),
            @ApiResponse(responseCode = "600", description = "사용자 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @GetMapping("/party-join")
    public ResponseEntity<List<InvitationRequestDto>> getJoinList(@AuthenticationPrincipal User user,
                                                                  @NotNull @RequestParam Role role) {
        List<InvitationRequestDto> invitationRequestDtos = partyService.getJoinList(user, role);
        return ResponseEntity.ok(invitationRequestDtos);
    }
}
