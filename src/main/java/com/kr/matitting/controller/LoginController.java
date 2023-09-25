package com.kr.matitting.controller;


import com.kr.matitting.dto.KakaoDto;
import com.kr.matitting.entity.MsgEntity;
import com.kr.matitting.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.net.http.HttpRequest;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping(value = "/auth")
    public String login(Model model) {
        //Kakao Url
        model.addAttribute("kakaoUrl", loginService.getKakaoLogin());

        return "/index";
    }

    @GetMapping(value = "/auth/kakao/callback")
    public ResponseEntity<MsgEntity> kakaoCallback(HttpServletRequest request) throws Exception {
        KakaoDto kakaoInfo = loginService.getKakaoInfo(request.getParameter("code"));

        if (kakaoInfo.getOuathId() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MsgEntity("fail", kakaoInfo));
        }

        else if (!loginService.checkUser(kakaoInfo)) { //신규 유저일 때
            return ResponseEntity.ok().body(new MsgEntity("new", kakaoInfo));
        }
        return ResponseEntity.ok().body(new MsgEntity("exist", kakaoInfo));
    }
}
