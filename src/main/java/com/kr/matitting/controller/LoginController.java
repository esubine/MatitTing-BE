package com.kr.matitting.controller;

import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping(value = "/home")
    public String home() {
        return "/new/home";
    }

    // OAuth2 로그인 시 최초 로그인인 경우 회원가입 진행, 필요한 정보를 쿼리 파라미터로 받는다
    @GetMapping("/oauth2/signUp")
    public ResponseEntity loadOAuthSignUp(@RequestParam String email, @RequestParam String socialType, @RequestParam String socialId, Model model) {
        model.addAttribute("email", email);
        model.addAttribute("socialType", socialType);
        model.addAttribute("socialId", socialId);

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("socialType", socialType);
        userInfo.put("socialId", socialId);
        return ResponseEntity.ok().body(userInfo);

        //thymeleaf test
//        return "/member/signupForm";
    }

    @PostMapping("/oauth2/signUp")
    public ResponseEntity loadOAuthSignUp(HttpServletRequest request, HttpServletResponse response) {
        User user = User.builder().email(request.getParameter("email")).build();
        String newUserEmail = userService.signUp(user);
        if (newUserEmail == null) {
            return ResponseEntity.badRequest().body("회원가입 실패!");
        }
        return ResponseEntity.ok().body("회원가입 성공!");
    }

    @GetMapping("/loginSuccess")
    public ResponseEntity success() {
        return ResponseEntity.ok("login success");

        //thymeleaf test
//        return "/member/loginSuccess";
    }


    @GetMapping("/renew")
    public ResponseEntity renewToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = jwtService.extractRefreshToken(request).get();
            return ResponseEntity.ok("BEARER " + jwtService.renewToken(refreshToken));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body("Refresh Token 이 만료되었습니다");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
