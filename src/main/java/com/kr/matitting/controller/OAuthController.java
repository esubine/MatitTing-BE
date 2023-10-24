package com.kr.matitting.controller;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2/")
public class OAuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "로그인", description = "사용자 로그인 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "600", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserExceptionType.class)))
    })
    @GetMapping("login")
    public ResponseEntity<User> loadOAuthSignUp(HttpServletResponse response,
                                          @RequestParam @NotNull String email,
                                          @RequestParam @NotNull SocialType socialType,
                                          @RequestParam @NotNull String socialId,
                                          @RequestParam @NotNull Role role,
                                          @RequestParam(required = false) String accessToken,
                                          @RequestParam(required = false) String refreshToken) {
        User user = null;
        if (role == Role.GUEST) {
            user = User.builder()
                    .id(0L)
                    .socialId(socialId)
                    .socialType(socialType)
                    .email(email)
                    .role(role)
                    .age(0)
                    .gender(Gender.ALL)
                    .nickname("")
                    .imgUrl("")
                    .build();
            return ResponseEntity.ok().body(user);
        }
        else if (role == Role.USER) {
            response.addHeader(jwtService.getAccessHeader(), accessToken);
            response.addHeader(jwtService.getRefreshHeader(), refreshToken);

            user = userService.getMyInfo(socialType, socialId);
            return ResponseEntity.ok().body(user);
        }
        return ResponseEntity.badRequest().body(null);
    }
    @Operation(summary = "회원가입", description = "회원가입 API 입니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = User.class)))
    @PostMapping("signUp")
    public ResponseEntity<User> loadOAuthSignUp(UserSignUpDto userSignUpDto) {
        User user = userService.signUp(userSignUpDto);
        return ResponseEntity.ok().body(user);
    }

    @Operation(summary = "로그인 성공", description = "로그인 성공 API 입니다.")
    @ApiResponse(responseCode = "200", description = "로그인성공")
    @GetMapping("loginSuccess")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("login success");
    }

    @Operation(summary = "로그아웃", description = "로그아웃 성공 API 입니다.")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request, "accessToken");
        userService.logout(accessToken);
        return ResponseEntity.ok("logout Success");
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴 성공 API 입니다.")
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @DeleteMapping("withdraw")
    public ResponseEntity<String> withdraw(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request, "accessToken");
        userService.withdraw(accessToken);
        return ResponseEntity.ok("withdraw Success");
    }

    @Operation(summary = "토큰 재발급", description = "AccessToken 재발급 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
        @ApiResponse(responseCode = "1200", description = "Refresh Token이 없습니다.", content = @Content(schema = @Schema(implementation = TokenExceptionType.class)))
    })
    @GetMapping("renewToken")
    public ResponseEntity<String> renewToken(HttpServletRequest request) {
        String refreshToken = jwtService.extractToken(request, "refreshToken");
        return ResponseEntity.ok("BEARER " + jwtService.renewToken(refreshToken));
    }
}
