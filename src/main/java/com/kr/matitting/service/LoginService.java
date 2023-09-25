package com.kr.matitting.service;

import com.kr.matitting.dto.KakaoDto;
import com.kr.matitting.entity.Member;
import com.kr.matitting.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final MemberRepository memberRepository;
    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final static String KAKAO_AUTH_URI = "https://kauth.kakao.com";
    private final static String KAKAO_API_URI = "https://kapi.kakao.com";

    /**
     * kakao 로그인 page
     */
    public String getKakaoLogin() {
        return KAKAO_AUTH_URI + "/oauth/authorize"
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URI
                + "&response_type=code";
    }

    /**
     * kakao 로그인 후 받은 code로 token 및 사용자 정보 Get
     */
    public KakaoDto getKakaoInfo(String code) throws Exception {
        if (code == null) throw new NotFoundException("Failed get authorization code");

        String accessToken = "";
        String refreshToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", KAKAO_CLIENT_ID);
            params.add("client_secret", KAKAO_CLIENT_SECRET);
            params.add("code", code);
            params.add("redirect_uri", KAKAO_REDIRECT_URI);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            // code -> accessToken, refreshToken
            ResponseEntity<String> response = restTemplate.exchange(
                    KAKAO_AUTH_URI + "/oauth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = (String) jsonObject.get("access_token");
            refreshToken = (String) jsonObject.get("refresh_token");

        } catch (Exception e) {
            throw new Exception("API call failed");
        }
        return getUserInfoWithToken(accessToken);
    }

    /**
     * accessToken -> 사용자 정보 Get
     */
    private KakaoDto getUserInfoWithToken(String accessToken) throws Exception {
        //HttpHeader 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer "+ accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");


        //HttpHeader 담기
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_API_URI + "/v2/user/me",
                HttpMethod.POST,
                httpEntity,
                String.class
        );

        //Response 데이터 파싱
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
        JSONObject account = (JSONObject) jsonObject.get("kakao_account");
        JSONObject profile = (JSONObject) account.get("profile");


        Long oauthId = (Long) jsonObject.get("id");
        String email = String.valueOf(account.get("email"));
        String img = String.valueOf(profile.get("profile_image_url"));

//        String birthDay = String.valueOf(account.get("birthday"));
//        String gender = String.valueOf(account.get("gender"));
//        String nickname = String.valueOf(profile.get("nickname"));


        return KakaoDto.builder().ouathId(oauthId).email(email).imgUrl(img).build();
    }

    public boolean checkUser(KakaoDto kakaoDto) {
        Member findMember = memberRepository.findByOauthId(kakaoDto.getOuathId());
        if (findMember == null) {
            return false;
        }
        return true;
    }
}
