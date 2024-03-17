package com.kr.matitting.controller;

import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.constant.Role;
import com.kr.matitting.dto.ResponseLoginDto;
import com.kr.matitting.dto.UserLoginDto;
import com.kr.matitting.dto.UserSignUpDto;
import com.kr.matitting.entity.User;
import com.kr.matitting.exception.token.TokenException;
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

    @Operation(summary = "소셜 로그인", description = "로그인 API \n\n" +
            "인증 Code를 전달받아서 Social Server와 통신하며 사용자 정보를 받아오는 로직 \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. Social 인증 Code를 전달받는다. \n\n" +
            "2. 인증 Code를 활용하여 accessToken 요청 URI를 작성한뒤 social server에 요청을 보내 accessToken을 받는다. \n\n" +
            "3. 받은 accessToken을 활용하여 사용자 정보 요청 URI를 작성한 뒤 요청을 보낸다. \n\n" +
            "4. 사용자 정보를 DB에서 찾아 신규, 기존 유저인지 판별 후 해당하는 값들을 response \n\n" +
            "Response header : accessToken, refreshToken (기존 유저일 때만 해당) \n\n" +
            "Response body : newUserId"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = ResponseLoginDto.class))),
        @ApiResponse(responseCode = "400(1104)", description = "유효하지 않은 소셜 Token\n\n 소셜 서버에서 가져온 Token이 유효하지 않을 때 발생", content = @Content(schema = @Schema(implementation = TokenException.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> socialLogin(@Valid @RequestBody OauthReq oauthReq) {
        log.info("넘겨받은 소셜 타입 :: " + oauthReq.getOauthProvider());
        log.info("넘겨받은 코드 :: " + oauthReq.getCode());
        log.info("넘겨받은 상태값 :: " + oauthReq.getState());

        UserLoginDto userLoginDto;
        if (oauthReq.getOauthProvider() == OauthProvider.KAKAO) {
            log.info("KAKAO 요청 됌");
            log.info("요청 정보 : ", oauthReq);
            userLoginDto = oauthService.getMemberByOauthLogin(new KakaoParams(oauthReq.getCode()));
        } else {
            log.info("NAVER 요청 됌");
            log.info("요청 정보 : ", oauthReq);
            userLoginDto = oauthService.getMemberByOauthLogin(new NaverParams(oauthReq.getCode(), oauthReq.getState()));
        }

        if (userLoginDto.role() == Role.USER) {
            //응답 헤더 생성
            log.info("기존 사용자이므로 token 생성");
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(jwtService.getAccessHeader(), "Bearer "+ userLoginDto.accessToken());
            httpHeaders.add(jwtService.getRefreshHeader(), "Bearer "+userLoginDto.refreshToken());
            return ResponseEntity.ok().headers(httpHeaders).body(new ResponseLoginDto(null));
        } else {
            log.info("신규 유저이므로 id만 response");
            return ResponseEntity.ok(new ResponseLoginDto(userLoginDto.userId()));
        }
    }

    @Operation(summary = "회원가입", description = "회원가입 API \n\n" +
            "사용자가 신규 회원일 때 추가 정보를 입력받아 DB 정보를 Update 시켜주는 API \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. 사용자의 ID와 추가로 입력 받은 값(성별, 생년월일, 닉네임)을 Request 받는다. \n\n" +
            "2. 사용자 ID 값으로 해당 사용자를 찾아서 추가로 입력받은 값을 업데이트 해준다. \n\n" +
            "3. 사용자의 Role을 GUEST -> USER로 변환한다. \n\n" +
            "Response header : accessToken, refreshToken \n\n" +
            "Response body : null \n\n" +
            "※ 회원가입을 진행할 때 nickname 값이 중복일 때 Exception이 발생합니다. 해당 정보는 아래와 같습니다. \n\n" +
            "" +
            "Response body 값들 중 Column이 누락되었을 때: {errorCode: 2002, errorMessage: 요청된 값이 유효하지 않습니다} \n\n" +
            "Response body 값 중 nickname이 중복되었을 때: {errorCode: 2003, errorMessage: 데이터 무결성 제약 조건 위반}" +
            "status code : 400"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "회원가입 성공"),
        @ApiResponse(responseCode = "403(602)", description = "권한이 없는 사용자, Role이 유효하지 않음", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody UserSignUpDto userSignUpDto) {
        User user = userService.signUp(userSignUpDto);
        String accessToken = jwtService.createAccessToken(user);
        String refreshToken = jwtService.createRefreshToken(user);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(jwtService.getAccessHeader(), "Bearer "+ accessToken);
        httpHeaders.add(jwtService.getRefreshHeader(), "Bearer "+ refreshToken);
        return ResponseEntity.status(HttpStatus.CREATED).headers(httpHeaders).body(null);
    }

    @Operation(summary = "로그아웃", description = "로그아웃 API \n\n" +
            "로그아웃 시 사용자의 인증/인가에 필요한 토큰을 제거하는 API \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. [request header] \"Authorization\"에 accessToken을 담은 요청이 들어오면 해당 reuqest를 받아 Token을 꺼낸다. \n\n" +
            "2. accessToken의 유효시간을 검사하여 남은 시간만큼 Black List에 등록한다. => Black List에 들어가면 accessToken 검사 시 Pass 하지 못한다. \n\n" +
            "3. accessToken에서 꺼낸 사용자의 SocialId로 refreshToken을 삭제한다. \n\n" +
            "Response Body : logout Success"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400(1100)", description = "AccessToken이 존재하지 않음", content = @Content(schema = @Schema(implementation = TokenException.class))),
        @ApiResponse(responseCode = "401(1102)", description = "AccessToken 검증 실패\n\n AccessToken 값이 유효하지 않거나 Expired 됐을 때 발생", content = @Content(schema = @Schema(implementation = TokenException.class)))
    })
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
                                                "4. 사용자의 정보를 DB에서 조회하여 삭제한다. \n\n" +
                                                "Response Body : withdraw Success"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "호원 탈퇴 성공", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "400(1100)", description = "AccessToken이 존재하지 않음", content = @Content(schema = @Schema(implementation = TokenException.class))),
        @ApiResponse(responseCode = "401(1102)", description = "AccessToken 검증 실패\n\n AccessToken 값이 유효하지 않거나 Expired 됐을 때 발생", content = @Content(schema = @Schema(implementation = TokenException.class)))
    })
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
                                                    "4. 위의 검사가 모두 통과되었을 시 accessToken을 새로 발급받아 [request header] \"Authorization\"에 담아서 response \n\n" +
                                                    "Response Header : accessToken \n\n" +
                                                    "Response Body : Success"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404(600)", description = "회원 정보가 없습니다.", content = @Content(schema = @Schema(implementation = UserException.class))),
        @ApiResponse(responseCode = "400(1200)", description = "Refresh Token이 없습니다.", content = @Content(schema = @Schema(implementation = TokenException.class)))
    })
    @GetMapping("/renew-token")
    public ResponseEntity<String> renewToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.extractToken(request, "refreshToken");
        String accessToken = jwtService.renewToken(refreshToken);
        response.setHeader(jwtService.getAccessHeader(), "Bearer "+accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body("Success");
    }

}
