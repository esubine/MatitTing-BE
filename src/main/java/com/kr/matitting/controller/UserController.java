package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.exception.team.TeamExceptionType;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필사진 혹은 닉네임을 수정해주는 메소드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @PatchMapping("/api/profile")
    public void myProfileUpdate(@RequestBody UserUpdateDto userUpdateDto) {
        userService.update(userUpdateDto);
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴 성공 API 입니다.")
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @DeleteMapping("/api/withdraw")
    public ResponseEntity<String> withdraw(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request, "accessToken");
        userService.withdraw(accessToken);
        return ResponseEntity.ok("withdraw Success");
    }
}
