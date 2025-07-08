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

        // 1. 토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getRole()
        );

        String refreshToken = jwtTokenizer.createRefreshToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getRole()
        );

        // 2. RefreshToken Redis에 저장
        refreshTokenService.saveRefreshToken(
                user.getId(),
                refreshToken,
                JwtTokenizer.REFRESH_TOKEN_EXPIRE_TIME
        );

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
