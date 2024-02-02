package com.kr.matitting.oauth2.dto;

import com.kr.matitting.constant.OauthProvider;
import org.springframework.util.MultiValueMap;

public interface OauthParams {
    public OauthProvider oauthProvider();
    public String getAuthorizationCode();
    public MultiValueMap<String, String> makeBody();
}
