package com.kr.matitting.controller;

import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("signUp")
    public ResponseEntity<Map<String, String>> loadOAuthSignUp(@RequestParam String email, @RequestParam String socialType, @RequestParam String socialId) {
        if (email == null || socialId == null || socialType == null) {
            throw new UserException(UserExceptionType.NULL_POINT_USER);
        }
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("socialType", socialType);
        userInfo.put("socialId", socialId);
        return ResponseEntity.ok().body(userInfo);
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
    public ResponseEntity<String> renewToken(HttpServletRequest request) throws Exception {
        String refreshToken = jwtService.extractToken(request).orElseThrow(() -> new TokenException(TokenExceptionType.INVALID_REFRESH_TOKEN));
        return ResponseEntity.ok("BEARER " + jwtService.renewToken(refreshToken));
    }
}
