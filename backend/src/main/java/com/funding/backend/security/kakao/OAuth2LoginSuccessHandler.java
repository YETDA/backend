package com.funding.backend.security.kakao;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // 1. 매 로그인 시 AccessToken은 새로 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getRole()
        );

        // 2. RefreshToken은 Redis에 있으면 재사용, 없으면 발급 및 저장
        String refreshToken = refreshTokenService.getRefreshToken(user.getId());
        if (refreshToken == null) {
            refreshToken = jwtTokenizer.createRefreshToken(
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRole().getRole()
            );

            refreshTokenService.saveRefreshToken(
                    user.getId(),
                    refreshToken,
                    JwtTokenizer.REFRESH_TOKEN_EXPIRE_TIME
            );
        }

        // 3. JSON 응답
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                String.format(
                        "{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}",
                        accessToken,
                        refreshToken
                )
        );
    }
}
