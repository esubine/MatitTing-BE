package com.kr.matitting.oauth2.client;

import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.oauth2.dto.OauthMember;
import com.kr.matitting.oauth2.dto.OauthParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

public interface OauthClient {
    public OauthProvider oauthProvider();

    public String getOauthLoginToken(OauthParams oauthParams);

    public OauthMember getMemberInfo(String accessToken);
}
