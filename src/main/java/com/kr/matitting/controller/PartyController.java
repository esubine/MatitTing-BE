package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.*;
import com.kr.matitting.exception.Map.MapException;
import com.kr.matitting.exception.Map.MapExceptionType;
import com.kr.matitting.exception.party.PartyException;
import com.kr.matitting.exception.partyjoin.PartyJoinException;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.service.PartyService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/party")
public class PartyController {
    private final PartyService partyService;
    private final UserService userService;

    // 파티 모집 글 생성
    @Operation(summary = "파티 글 생성", description = "파티 글 생성 API 입니다. \n\n" +
                                                    "[로직 설명] \n\n" +
                                                    "1. 파티 생성에 필요한 데이터들을 request로 받습니다.\n\n" +
                                                    "\t 1-1. 파티 생성 dto 중 thumbnail은 필수값이 아닙니다. thumbnail이 없는 경우 category에 맞춰 thumbnail이 설정됩니다.\n\n" +
                                                    "\t 1-2. 파티 마감시간은 '파티시간의 -1시간'으로 자동 설정됩니다.\n\n" +
                                                    "\t 1-3. 파티 생성 dto 중 totalParticipant는 2이상이어야 합니다.\n\n" +
                                                    "2. 1의 유효성 검사를 거친 후 request값에 따라 파티를 생성합니다.\n\n" +
                                                    "※ 파티 생성 완료 시 채팅방이 자동으로 생성됩니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = ResponseCreatePartyDto.class))),
        @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "1300", description = "카카오 맵 Authorization이 실패했습니다. \n\n *kakao map api 관련 설정값이 잘못되어 발생하는 에러입니다.", content = @Content(schema = @Schema(implementation = MapExceptionType.class))),
        @ApiResponse(responseCode = "1301", description = "카카오 맵에서 데이터를 받아오지 못했습니다. \n\n *위도, 경도값이 유효하지 않아 카카오맵에서 데이터 조회, 주소변환이 되지 않을때 발생합니다.", content = @Content(schema = @Schema(implementation = MapExceptionType.class))),
        @ApiResponse(responseCode = "1302", description = "카카오 맵 서버 오류입니다. \n\n *카카오맵 자체 오류입니다..", content = @Content(schema = @Schema(implementation = MapException.class)))
    })

    @PostMapping
    public ResponseEntity<ResponseCreatePartyDto> createParty(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid PartyCreateDto request
    ) {
        ResponseCreatePartyDto responseCreatePartyDto = partyService.createParty(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreatePartyDto);
    }

    @Operation(summary = "파티 업데이트", description = "파티 정보 업데이트 API \n\n" +
                                                    "파티 방장이 파티의 정보를 수정하는 API \n\n \n\n" +
                                                    "로직 설명 \n\n" +
                                                    "1. 수정 된 값에 대해서 request를 받는다. \n\n" +
                                                    "2. request 값을 검사하고 값이 들어있는 경우 DB에서 해당 값을 수정하여 Update를 진행 \n\n" +
                                                    "※ 파티의 위도 경도를 수정 시 => 파티의 address 값은 kakao Map API를 기준으로 자동 Update 되게 됩니다. \n\n" +
                                                    "※ 파티의 시작 시간을 수정 시 => 파티의 모집 마감시간은 시작 시간 - 1시간으로 자동 Update 되게 됩니다. \n\n" +
                                                    "※ 파티의 모집 인원을 수정 시 => 현재 파티에 참여한 인원보다 작은 인원으로 수정 시 Exception이 발생하게 됩니다.\n\n" +
                                                    "※ 파티의 상태를 모집 완료로 수정 시 => 파티 참여자들에게 모집 완료 알림을 전송하게 됩니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파티 업데이트 성공", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404(800)", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyException.class))),
        @ApiResponse(responseCode = "400(801)", description = "올바르지 못한 REQUEST 값.", content = @Content(schema = @Schema(implementation = PartyException.class))),
        @ApiResponse(responseCode = "403(602)", description = "요청한 회원정보가 잘못되었습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
    })
    @PatchMapping("/{partyId}")
    public ResponseEntity<String> updateParty(@AuthenticationPrincipal Long userId, @RequestBody PartyUpdateDto partyUpdateDto, @PathVariable Long partyId) {
        partyService.partyUpdate(userId, partyUpdateDto, partyId);
        return ResponseEntity.ok("Success Party update");
    }

    @Operation(summary = "파티 세부정보", description = "파티의 세부정보 API \n\n" +
                                                    "파티의 세부 정보를 보여주는 API \n\n \n\n" +
                                                    "로직 설명 \n\n" +
                                                    "1. 파티 ID로 DB에서 검색하여 파티 정보를 불러오게 됩니다. \n\n" +
                                                    "2. 정보를 response 할 때 해당 파티에 대해서 내가 방장인지 아닌지에 대한 여부를 추가로 response \n\n" +
                                                    "※ 해당 파티의 조회수 +1"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파티 세부정보 조회 성공", content = @Content(schema = @Schema(implementation = ResponsePartyDetailDto.class))),
        @ApiResponse(responseCode = "404(800)", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyException.class)))
    })
    @GetMapping("/{partyId}")
    public ResponseEntity<ResponsePartyDetailDto> partyDetail(@AuthenticationPrincipal Long userId, @PathVariable Long partyId) {
        ResponsePartyDetailDto partyInfo = partyService.getPartyInfo(userId, partyId);
        return ResponseEntity.ok(partyInfo);
    }

    @Operation(summary = "파티 삭제", description = "파티를 삭제하는 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파티 삭제 성공", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "403(602)", description = "권한이 없는 사용자, Role이 유효하지 않음", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "404(800)", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyException.class)))
    })
    @DeleteMapping("/{partyId}")
    public ResponseEntity<String> partyDelete(@AuthenticationPrincipal Long userId, @PathVariable Long partyId) {
        partyService.deleteParty(userId, partyId);
        return ResponseEntity.ok("Success Party Delete");
    }

    @Operation(summary = "파티 참가 신청", description = "파티 참가 신청 API \n\n" +
                                                    "사용자가 해당 파티에 참가 or 참기 신청 취소 요청을 하는 API \n\n \n\n" +
                                                    "로직 설명 \n\n" +
                                                    "1. 사용자가 해당 파티에 ACCEPT or CANCEL 요청을 보내면 request로 해당 값을 전달받는다. \n\n" +
                                                    "2. ACCEPT일 경우 해당 요청이 이전에 있었는지 확인 후 있으면 Exception, 없으면 신청에 대한 정보가 DB에 입력된다. \n\n" +
                                                    "3. CANCEL일 경우 해당 요청이 DB에 있는지 확인 후 없으면 Exception, 있으면 내가 신청한 정보를 DB에서 삭제한다. \n\n" +
                                                    "※ ACCEPT or CANCEL 입력이 아닐 시에는 Exception"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파티 참가 신청 성공", content = @Content(schema = @Schema(implementation = ResponseCreatePartyJoinDto.class))),
        @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "404(700)", description = "참가 신청 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyJoinException.class))),
        @ApiResponse(responseCode = "404(800)", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyException.class))),
        @ApiResponse(responseCode = "403(602)", description = "요청한 회원정보가 잘못되었습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
    })
    @PostMapping("/participation")
    public ResponseEntity<ResponseCreatePartyJoinDto> JoinParty(@RequestBody @Valid PartyJoinDto partyJoinDto, @AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(partyService.joinParty(partyJoinDto, userId));
    }

    @Operation(summary = "파티 참가 수락/거절", description = "참여 수락/거절 API \n\n " +
                                                        "방장이 참여신청에 대한 수락/거절을 결정하는 API \n\n \n\n" +
                                                        "로직 설명 \n\n" +
                                                        "1. 방장이 특정 사용자의 신청 요청에 대해서 수락/거절을 내린 정보를 request 받는다. \n\n" +
                                                        "2. ACCEPT일 경우 파티방 인원의 수를 +1하고, 파티 Team DB에 사용자 값을 추가해준다. \n\n" +
                                                        "3. REFUSE일 경우 아무런 동작을 진행하지 않는다. \n\n" +
                                                        "4. 사용자 요청에 대해서 응답이 진행되었으므로, PartyJoin DB에서 사용자가 신청한 신청 데이터를 삭제한다. \n\n" +
                                                        "※ ACCEPT을 진행할 때 party 모집 인원이 만석일 경우는 Exception")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파티 참가 수락/거절 성공", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "404(700)", description = "참가할 파티를 찾지 못했습니다.", content = @Content(schema = @Schema(implementation = PartyJoinException.class))),
        @ApiResponse(responseCode = "404(800)", description = "파티 정보가 없습니다.", content = @Content(schema = @Schema(implementation = PartyException.class))),
        @ApiResponse(responseCode = "403(602)", description = "요청한 회원정보가 잘못되었습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
    })
    @PostMapping("/decision")
    public ResponseEntity<String> AcceptRefuseParty(@RequestBody @Valid PartyDecisionDto partyDecisionDto, @AuthenticationPrincipal Long userId) {
        String result = partyService.decideUser(partyDecisionDto, userId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "파티 현황", description = "내 파티 현황 API \n\n" +
                                                "내가 속해있는 파티의 현황을 불러오는 API \n\n \n\n" +
                                                "로직 설명 \n\n" +
                                                "role \n\n" +
                                                "\t HOST => 내가 방장으로 있는 파티 리스트를 반환 \n\n" +
                                                "\t VOLUNTEER => 내가 참여한 파티 리스트를 반환 \n\n" +
                                                "status \n\n" +
                                                "\t RECRUIT => 모집중인 파티 리스트를 반환 \n\n" +
                                                "\t RECRUIT_FINISH => 모집 완료 파티 리스트를 반환 \n\n" +
                                                "\t PARTY_FINISH => 마감된 파티 리스트를 반환 \n\n" +
                                                "pagealbe \n\n" +
                                                "\t page : 페이지 넘버 \n\n" +
                                                "\t size : 받아올 객체 리스트 개수 \n\n" +
                                                "\t sort : 최신순으로 정렬하여 response하도록 설정 request X \n\n")
    @ApiResponse(responseCode = "200", description = "파티 현황 조회 성공", content = @Content(schema = @Schema(implementation = ResponseMyParty.class)))
    @GetMapping("/party-status")
    public ResponseEntity<ResponseMyParty> myPartyList(@AuthenticationPrincipal Long userId,
                                                       @PageableDefault Pageable pageable,
                                                       @Valid PartyStatusReq partyStatusReq) {
        return ResponseEntity.ok(userService.getMyPartyList(userId, partyStatusReq, pageable));
    }

    @Operation(summary = "파티 신청 현황", description = "실시간 파티 현황 API \n\n" +
            "내가 파티 참가요청을 보냈거나, 내 파티에 참가 요청이 온 현황을 불러오는 API \n\n \n\n" +
            "로직 설명 \n\n" +
            "role \n\n" +
            "\t HOST => 내가 방장으로 존재하는 파티에 사용자들이 참가 요청을 보낸 리스트를 반환 \n\n" +
            "\t VOLUNTEER => 내가 상대방의 파티에 참가 요청을 보낸 리스트를 반환")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파티 신청 현황 조회 성공", content = @Content(schema = @Schema(implementation = ResponseGetPartyJoinDto.class))),
        @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "403(602)", description = "권한이 없는 사용자, Role이 유효하지 않음", content = @Content(schema = @Schema(implementation = UserException.class))),
    })
    @GetMapping("/party-join")
    public ResponseEntity<ResponseGetPartyJoinDto> getJoinList(@AuthenticationPrincipal Long userId,
                                                               @PageableDefault Pageable pageable,
                                                               @NotNull @RequestParam Role role) {
        ResponseGetPartyJoinDto joinList = partyService.getJoinList(userId, role, pageable);
        return ResponseEntity.ok(joinList);
    }
}
