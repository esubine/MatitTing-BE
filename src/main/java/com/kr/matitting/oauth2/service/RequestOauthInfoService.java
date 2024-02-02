package com.kr.matitting.oauth2.service;

import com.kr.matitting.constant.OauthProvider;
import com.kr.matitting.oauth2.client.OauthClient;
import com.kr.matitting.oauth2.dto.OauthMember;
import com.kr.matitting.oauth2.dto.OauthParams;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOauthInfoService {
    // Provider : Client
    private final Map<OauthProvider, OauthClient> clients;

    public RequestOauthInfoService(List<OauthClient> clients) {
        this.clients = clients.stream().collect(
                Collectors.toUnmodifiableMap(OauthClient::oauthProvider, Function.identity()));
    }

    public OauthMember request(OauthParams oauthParams) {
        //oauth provider 요청에 맞는 client get
        OauthClient client = clients.get(oauthParams.oauthProvider());
        //client에서 accessToken을 가져오는 로직 실행
        String accessToken = client.getOauthLoginToken(oauthParams);
        //accessToken으로 유저 정보를 가져오는 로직 실행
        return client.getMemberInfo(accessToken);
    }
}
