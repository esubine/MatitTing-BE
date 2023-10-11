package com.kr.matitting.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.exception.user.UserExceptionType;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
    public ResponseEntity<String> loadOAuthSignUp(HttpServletRequest request) {
        User user = User.builder().email(request.getParameter("email")).build();
        String newUserEmail = userService.signUp(user);
        if (newUserEmail == null) {
            return ResponseEntity.badRequest().body("회원가입 실패!");
        }
        return ResponseEntity.ok().body("회원가입 성공!");
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
        try {
            String refreshToken = jwtService.extractToken(request).get();
            return ResponseEntity.ok("BEARER " + jwtService.renewToken(refreshToken));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Refresh Token 이 만료되었습니다");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
