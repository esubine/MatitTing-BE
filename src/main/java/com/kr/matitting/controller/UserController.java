package com.kr.matitting.controller;

import com.kr.matitting.dto.ResponseMyInfo;
import com.kr.matitting.dto.UserUpdateDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "내 프로필 조회", description = "사용자 본인의 프로필을 조회하는 API \n\n" +
                                                    "내 프로필 정보와 파티현황-모집중 정보를 불러온다. \n\n \n\n" +
                                                    "로직 설명 \n\n" +
                                                    "1. URI에서 받아온 userId와 Token 값에서의 userId와 비교하여 본인인지 판단한다. \n\n" +
                                                    "2. 본인일 경우에는 DB에서 나의 정보를 가져와서 response")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @GetMapping
    public ResponseEntity<ResponseMyInfo> myProfile(@AuthenticationPrincipal User user) {
        ResponseMyInfo myInfo = userService.getMyInfo(user);
        return ResponseEntity.ok(myInfo);
    }

    @Operation(summary = "프로필 업데이트", description = "사용자 프로필 업데이트 API \n\n" +
                                                        "본인의 프로필 정보를 수정한다. => 수정 가능한 것은 닉네임, 프로필 이미지 \n\n \n\n" +
                                                        "로직 설명 \n\n" +
                                                        "1. request 받은 userId로 DB에서 사용자의 정보를 가져온다. \n\n" +
                                                        "2. Dto에 들어있는 값을 확인하여 사용자 정보를 업데이트 한다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @PatchMapping
    public ResponseEntity<String> myProfileUpdate(@RequestBody UserUpdateDto userUpdateDto, @AuthenticationPrincipal User user) {
        userService.update(user, userUpdateDto);
        return ResponseEntity.ok("Success Profile Update");
    }
}
