package com.kr.matitting.oauth2.dto;

import com.kr.matitting.constant.OauthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Getter
@AllArgsConstructor
public class NaverParams implements OauthParams{
    private String authorizationCode;
    private String state;

    @Override
    public OauthProvider oauthProvider() {
        return OauthProvider.NAVER;
    }

    @Override
    public String getAuthorizationCode() {
        return authorizationCode;
    }

    @Override
    public MultiValueMap<String, String> makeBody() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", authorizationCode);
        body.add("state", state);
        return body;
    }
}
