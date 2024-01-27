package com.kr.matitting.oauth2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kr.matitting.constant.OauthProvider;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverMember implements OauthMember{

    @JsonProperty("response")
    private Response response;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Response{
        private String id;
        private String email;
        private String nickname;
    }

    @Override
    public String getSocialId() {
        return response.id;
    }

    @Override
    public String getEmail() {
        return response.email;
    }

    @Override
    public String getNickname() {
        return response.nickname;
    }

    @Override
    public OauthProvider getOauthProvider() {
        return OauthProvider.NAVER;
    }
}
