package com.funding.backend.security.oauth.provider;

import com.funding.backend.domain.role.entity.Role;
import com.funding.backend.domain.role.repository.RoleRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.RoleType;
import com.funding.backend.enums.UserActive;
import com.funding.backend.security.oauth.model.CustomOAuth2User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOAuthUserParser {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OAuth2User parse(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String socialId = String.valueOf(attributes.get("id")); // 카카오 id
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String email = (String) kakaoAccount.get("email");
        String image = (String) profile.get("profile_image_url");
        String provider = "kakao";

        // 사용자 조회 or 신규 가입
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
                            .userActive(UserActive.ACTIVE)
                            .build());
                });

        return new CustomOAuth2User(user, attributes);
    }
}
