package com.funding.backend.security.kakao;

import com.funding.backend.domain.role.entity.Role;
import com.funding.backend.domain.role.repository.RoleRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.RoleType;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(request);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = request.getClientRegistration().getRegistrationId(); // kakao
        String socialId = String.valueOf(attributes.get("id")); // 카카오 id

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email");
        String image = (String) profile.get("profile_image_url");

        // 사용자 조회 or 가입
        User user = userRepository.findBySocialIdAndSsoProvider(socialId, provider)
                .orElseGet(() -> {
                    Role role = roleRepository.findByRole(RoleType.USER)
                            .orElseThrow(() -> new RuntimeException("USER Role 없음"));

                    return userRepository.save(User.builder()
                            .socialId(socialId)
                            .ssoProvider(provider.toUpperCase())
                            .role(role)
                            .name(nickname)
                            .email(email)
                            .image(image)
                            .build());
                });

        return new CustomOAuth2User(user, attributes);
    }
}
