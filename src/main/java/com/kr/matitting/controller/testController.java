package com.kr.matitting.controller;

import com.kr.matitting.constant.Gender;
import com.kr.matitting.constant.Role;
import com.kr.matitting.constant.SocialType;
import com.kr.matitting.entity.User;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
//TODO 테스트 종료 후 삭제 예정
public class testController {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @GetMapping("/start/home")
    public String home(Model model) {
        return "/start/home";
    }

    @GetMapping("/matitting")
    public String dummy_data(HttpServletResponse response) {
        User user = User.builder()
                .socialId("12309812309128301")
                .socialType(SocialType.KAKAO)
                .email("test@kakao.com")
                .nickname("새싹개발자")
                .age(20)
                .imgUrl("증명사진100.jpg")
                .gender(Gender.FEMALE)
                .role(Role.USER)
                .build();
        userRepository.save(user);
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        response.addHeader(jwtService.getAccessHeader(), accessToken);
        response.addHeader(jwtService.getRefreshHeader(), refreshToken);

        return "success!";
    }
}