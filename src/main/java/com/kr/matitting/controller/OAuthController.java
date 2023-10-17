package com.kr.matitting.controller;

import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
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

    @GetMapping("login")
    public ResponseEntity loadOAuthSignUp(HttpServletResponse response,
                                          @RequestParam @NotNull String email,
                                          @RequestParam @NotNull SocialType socialType,
                                          @RequestParam @NotNull String socialId,
                                          @RequestParam @NotNull Role role,
                                          @RequestParam(required = false) String accessToken,
                                          @RequestParam(required = false) String refreshToken) {
        if (role == Role.GUEST) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("email", email);
            userInfo.put("socialType", socialType.toString());
            userInfo.put("socialId", socialId);
            userInfo.put("role", role.getKey());
            return ResponseEntity.ok().body(userInfo);
        }
        else if (role == Role.USER) {
            response.addHeader(jwtService.getAccessHeader(), accessToken);
            response.addHeader(jwtService.getRefreshHeader(), refreshToken);

            User user = userService.getMyInfo(socialType, socialId);
            return ResponseEntity.ok().body(user);
        }
        return ResponseEntity.badRequest().body("로그인이 실패했습니다.");
    }
    @PostMapping("signUp")
    public ResponseEntity<User> loadOAuthSignUp(UserSignUpDto userSignUpDto) {
        User user = userService.signUp(userSignUpDto);
        return ResponseEntity.ok().body(user);
    }

    @GetMapping("loginSuccess")
    public ResponseEntity<String> success() {
        return ResponseEntity.ok("login success");
    }

    @PostMapping("logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request).get();
        userService.logout(accessToken);
        return ResponseEntity.ok("logout Success");
    }

    @DeleteMapping("withdraw")
    public ResponseEntity<String> withdraw(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request).get();
        userService.withdraw(accessToken);
        return ResponseEntity.ok("withdraw Success");
    }

    @GetMapping("renewToken")
    public ResponseEntity<String> renewToken(HttpServletRequest request) {
        String refreshToken = jwtService.extractToken(request).orElseThrow(() -> new TokenException(TokenExceptionType.INVALID_REFRESH_TOKEN));
        return ResponseEntity.ok("BEARER " + jwtService.renewToken(refreshToken));
    }
}
