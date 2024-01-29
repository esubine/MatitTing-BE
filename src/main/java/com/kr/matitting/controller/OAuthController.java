package com.kr.matitting.controller;

import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.UserLoginDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.exception.token.TokenExceptionType;
import com.kr.matitting.exception.user.UserException;
import com.kr.matitting.jwt.service.JwtService;
import com.kr.matitting.oauth2.dto.KakaoParams;
import com.kr.matitting.oauth2.dto.NaverParams;
import com.kr.matitting.oauth2.dto.OauthReq;
import com.kr.matitting.oauth2.service.OauthService;
import com.kr.matitting.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth2")
public class OAuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final OauthService oauthService;

    @Operation(summary = "카카오 소셜 로그인", description = "로그인 API \n\n" +
            "인증 Code를 전달받아서 Social Server와 통신하며 사용자 정보를 받아오는 로직 \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. Social 인증 Code를 전달받는다. \n\n" +
            "2. 인증 Code를 활용하여 accessToken 요청 URI를 작성한뒤 social server에 요청을 보내 accessToken을 받는다. \n\n" +
            "3. 받은 accessToken을 활용하여 사용자 정보 요청 URI를 작성한 뒤 요청을 보낸다. \n\n" +
            "4. 사용자 정보를 DB에서 찾아 신규, 기존 유저인지 판별 후 해당하는 값들을 response \n\n" +
            "※ 신규 유저는 userId와 role(GUEST)를 기존 유저는 userId와 role(USER), accessToken, refreshToken을 발급하여 Response ※"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = Long.class))),
            @ApiResponse(responseCode = "404", description = "유저 NOT FOUND", content = @Content(schema = @Schema(implementation = UserException.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> kakaoCallback(@RequestBody OauthReq oauthReq) {
        log.debug("넘겨받은 kakao 인증키 :: " + oauthReq.getCode());

        UserLoginDto userLoginDto;
        if (oauthReq.getOauthProvider() == OauthProvider.KAKAO) {
            userLoginDto = oauthService.getMemberByOauthLogin(new KakaoParams(oauthReq.getCode()));
        } else {
            userLoginDto = oauthService.getMemberByOauthLogin(new NaverParams(oauthReq.getCode(), oauthReq.getState()));
        }

        if (userLoginDto.role() == Role.USER) {
            //응답 헤더 생성
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(jwtService.getAccessHeader(), userLoginDto.accessToken());
            httpHeaders.add(jwtService.getRefreshHeader(), userLoginDto.refreshToken());
            return ResponseEntity.ok().headers(httpHeaders).body(null);
        } else {
            return ResponseEntity.ok(userLoginDto.userId());
        }
    }

    @Operation(summary = "회원가입", description = "회원가입 API \n\n" +
            "사용자가 신규 회원일 때 추가 정보를 입력받아 DB 정보를 Update 시켜주는 API \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. 사용자의 ID와 추가로 입력 받은 값(성별, 생년월일, 닉네임)을 Request 받는다. \n\n" +
            "2. 사용자 ID 값으로 해당 사용자를 찾아서 추가로 입력받은 값을 업데이트 해준다. \n\n" +
            "3. 사용자의 Role을 GUEST -> USER로 변환한다."
    )
    @ApiResponse(responseCode = "201", description = "회원가입 성공")
    @PostMapping("signup")
    public ResponseEntity<?> loadOAuthSignUp(@Valid UserSignUpDto userSignUpDto) {
        userService.signUp(userSignUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 API \n\n" +
            "로그아웃 시 사용자의 인증/인가에 필요한 토큰을 제거하는 API \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. [request header] \"Authorization\"에 accessToken을 담은 요청이 들어오면 해당 reuqest를 받아 Token을 꺼낸다. \n\n" +
            "2. accessToken의 유효시간을 검사하여 남은 시간만큼 Black List에 등록한다. => Black List에 들어가면 accessToken 검사 시 Pass 하지 못한다. \n\n" +
            "3. accessToken에서 꺼낸 사용자의 SocialId로 refreshToken을 삭제한다. \n\n"
    )
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {

        String accessToken = jwtService.extractToken(request, "accessToken");
        userService.logout(accessToken);
        return ResponseEntity.ok("logout Success");
    }

    @Operation(summary = "회원탈퇴", description = "회원탈퇴 API \n\n" +
                                                "회원 탈퇴 시 사용자의 정보를 삭제하는 API \n\n \n\n" +
                                                "로직 설명 \n\n" +
                                                "1. [request header] \"Authorization\"에 accessToken을 담은 요청이 들어오면 해당 reuqest를 받아 Token을 꺼낸다. \n\n" +
                                                "2. accessToken의 유효시간을 검사하여 남은 시간만큼 Black List에 등록한다. => Black List에 들어가면 accessToken 검사 시 Pass 하지 못한다. \n\n" +
                                                "3. accessToken에서 꺼낸 사용자의 SocialId로 refreshToken을 삭제한다. \n\n" +
                                                "4. 사용자의 정보를 DB에서 조회하여 삭제한다."
    )
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(HttpServletRequest request) {
        String accessToken = jwtService.extractToken(request, "accessToken");
        userService.withdraw(accessToken);
        return ResponseEntity.ok("withdraw Success");
    }

    @Operation(summary = "토큰 재발급", description = "AccessToken 재발급 API \n\n" +
                                                    "accessToken의 유효기간이 끝났을 때 refreshToken을 활용하여 accessToken을 재발급 받는 API \n\n \n\n" +
                                                    "로직 설명 \n\n" +
                                                    "1. [request header] \"Authorization-Refresh\"에 refreshToken을 담은 요청이 들어오면 해당 request를 받아 Token을 꺼낸다. \n\n" +
                                                    "2. refreshToken에서 사용자의 SocialId를 꺼내 redis에서 해당 값을 가지고 있는 refreshToken이 있는지 검사하여 return한다. => redis는 key(socialId):value(refreshToken)로 관리되고 있음 \n\n" +
                                                    "3. 값이 있을 때 해당 refreshToken에서 꺼낸 socialId가 DB에 존재하는 유저의 SocialId 인지 검사 한다. \n\n" +
                                                    "4. 위의 검사가 모두 통과되었을 시 accessToken을 새로 발급받아 [request header] \"Authorization\"에 담아서 response"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "토큰 재발급 성공"),
        @ApiResponse(responseCode = "1200", description = "Refresh Token이 없습니다.", content = @Content(schema = @Schema(implementation = TokenExceptionType.class)))
    })
    @GetMapping("/renew-token")
    public ResponseEntity<String> renewToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.extractToken(request, "refreshToken");
        String accessToken = jwtService.renewToken(refreshToken);
        response.setHeader(jwtService.getAccessHeader(), "Bearer "+accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }

}
