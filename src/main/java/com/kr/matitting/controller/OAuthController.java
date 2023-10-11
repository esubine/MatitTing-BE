package com.kr.matitting.controller;

import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Tag(name = "인증", description = "인증 관련 api")
@Controller
@RequiredArgsConstructor
@RequestMapping("/oauth2/")
public class OAuthController {
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("signUp")
    public ResponseEntity loadOAuthSignUp(@RequestParam String email, @RequestParam String socialType, @RequestParam String socialId) {
        if (email == null || socialId == null || socialType == null) {
            throw new NullPointerException("신규 회원 Data 요청이 잘못되었습니다.");
        }
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("socialType", socialType);
        userInfo.put("socialId", socialId);
        return ResponseEntity.ok().body(userInfo);
    }

    @PostMapping("signUp")
    public ResponseEntity loadOAuthSignUp(HttpServletRequest request) {
        User user = User.builder().email(request.getParameter("email")).build();
        String newUserEmail = userService.signUp(user);
        if (newUserEmail == null) {
            return ResponseEntity.badRequest().body("회원가입 실패!");
        }
        return ResponseEntity.ok().body("회원가입 성공!");
    }

    @GetMapping("loginSuccess")
    public ResponseEntity success() {
        return ResponseEntity.ok("login success");
    }

    @PostMapping("logout")
    public ResponseEntity logout(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request).get();
        userService.logout(accessToken);
        return ResponseEntity.ok("logout Success");
    }
    
    @GetMapping("renewToken")
    public ResponseEntity renewToken(HttpServletRequest request) {
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
