package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.PartyCreateDto;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.team.TeamExceptionType;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/profile")
public class UserController {
    private final UserService userService;

    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필사진 혹은 닉네임을 수정해주는 메소드")
    @GetMapping("/{userId}")
    public ResponseEntity<User> myProfile(@PathVariable Long userId, @AuthenticationPrincipal User user) {
        User myInfo = userService.getMyInfo(userId, user);
        return ResponseEntity.ok(myInfo);
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<String> myProfileUpdate(@RequestBody UserUpdateDto userUpdateDto, @PathVariable Long userId) {
        userService.update(userId, userUpdateDto);
        return ResponseEntity.ok("Success Profile Update");
    }
}
