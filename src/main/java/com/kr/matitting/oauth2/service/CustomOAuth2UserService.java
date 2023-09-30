package com.kr.matitting.oauth2.service;

import com.kr.matitting.entity.Role;
import com.kr.matitting.entity.User;
import com.kr.matitting.entity.SocialType;
import com.kr.matitting.oauth2.CustomOauth2User;
import com.kr.matitting.oauth2.OAuthAttributes;
import com.kr.matitting.oauth2.userinfo.OAuth2UserInfo;
import com.kr.matitting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;

    private static final String KAKAO = "kakao";
    private static final String NAVER = "naver";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("CustomOAuth2UserService.loadUser() 실행 - OAuth2 로그인 요청 진입");

        /**
         * DefaultOAuth2UserService 객체를 생성하여, loadUser(userRequest)를 통해 DefaultOAuth2User 객체를 생성 후 반환
         * DefaultOAuth2UserService의 loadUser()는 소셜 로그인 API의 사용자 정보 제공 URI로 요청을 보내서
         * 사용자 정보를 얻은 후, 이를 통해 DefaultOAuth2User 객체를 생성 후 반환한다.
         * 결과적으로, OAuth2User는 OAuth 서비스에서 가져온 유저 정보를 담고 있는 유저
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes oauth2Attributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);
        OAuth2UserInfo oauth2UserInfo = oauth2Attributes.getOauth2UserInfo();
        String socialId = oauth2UserInfo.getSocialId();
        String email = oauth2UserInfo.getEmail();

        User user = getUser(socialId, email, socialType);


        return new CustomOauth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().getKey())),
                attributes,
                oauth2Attributes.getNameAttributeKey(),
                user);
    }
    private User getUser(String socialId, String email, SocialType socialType) {
        User user = userRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElse(User.builder().email(email).role(Role.GUEST).socialType(socialType).socialId(socialId).build());
        return user;
    }


    private SocialType getSocialType(String registrationId) {
        if (KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }

        if (NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        }
        return null;
    }
}
