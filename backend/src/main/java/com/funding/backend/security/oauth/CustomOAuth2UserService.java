package com.funding.backend.security.oauth;

import com.funding.backend.security.oauth.provider.GithubOAuthUserParser;
import com.funding.backend.security.oauth.provider.KakaoOAuthUserParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final GithubOAuthUserParser githubParser;
    private final KakaoOAuthUserParser kakaoParser;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();

        return switch (registrationId) {
            case "github" -> githubParser.parse(user);
            case "kakao" -> kakaoParser.parse(user);
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        };
    }
}