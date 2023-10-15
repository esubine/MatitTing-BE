package com.kr.matitting.oauth2.userinfo;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {
    public static Map<String, Object> responseMap;

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
        responseMap = (Map<String, Object>) attributes.get("response"); // attributes 에서 유저에 대한 정보만 추출하여 Map 으로 생성
    }

    @Override
    public String getSocialId() {
        return String.valueOf(responseMap.get("id"));
    }

    @Override
    public String getEmail() {
        return String.valueOf(responseMap.get("email"));
    }

    @Override
    public String getImageUrl(){
        return String.valueOf(responseMap.get("profile_image"));
    }
}
