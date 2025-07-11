package com.funding.backend.security.oauth.provider;

import com.funding.backend.domain.role.entity.Role;
import com.funding.backend.domain.role.repository.RoleRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.RoleType;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.oauth.model.CustomOAuth2User;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GithubOAuthUserParser {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public OAuth2User parse(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = "github";
        String socialId = String.valueOf(attributes.get("id")); // GitHub 유저 ID
        String email = (String) attributes.get("email"); // null일 수 있음
        String name = (String) attributes.get("login"); // GitHub username
        String image = (String) attributes.get("avatar_url");

        // 사용자 조회 or 신규 가입
        User user = userRepository.findBySocialIdAndSsoProvider(socialId, provider)
                .orElseGet(() -> {
                    Role role = roleRepository.findByRole(RoleType.USER)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ROLE_NOT_FOUND));

                    return userRepository.save(User.builder()
                            .socialId(socialId)
                            .ssoProvider(provider.toUpperCase()) // "GITHUB"
                            .role(role)
                            .name(name)
                            .email(email)
                            .image(image)
                            .userActive(UserActive.ACTIVE)
                            .build());
                });

        return new CustomOAuth2User(user, attributes);
    }
}
