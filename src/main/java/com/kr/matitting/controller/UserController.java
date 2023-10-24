package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.exception.team.TeamExceptionType;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필사진 혹은 닉네임을 수정해주는 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @PatchMapping("/api/profile")
    public void myProfileUpdate(UserUpdateDto userUpdateDto) {
        userService.update(userUpdateDto);
    }

    @Operation(summary = "파티 현황", description = "내 파티 현황을 불러오는 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "파티 현황 불러오기 성공", content = @Content(schema = @Schema(implementation = PartyCreateDto.class))),
            @ApiResponse(responseCode = "1000", description = "파티 팀 정보가 없습니다.", content = @Content(schema = @Schema(implementation = TeamExceptionType.class)))
    })
    @GetMapping("/api/partyStatus")
    public ResponseEntity<List<PartyCreateDto>> myPartyList(@RequestParam Long userId, @RequestParam Role role) {
        List<PartyCreateDto> myPartyList = userService.getMyPartyList(userId, role);
        return ResponseEntity.ok().body(myPartyList);
    }
}
