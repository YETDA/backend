package com.funding.backend.security.kakao;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // 최초 회원가입 판단
        boolean isNewUser = user.getUserActive() == null;

        // 프로필 이미지 최초 저장
        if (user.getImage() != null && user.getImage().contains("k.kakaocdn.net")) {
            String kakaoImageUrl = user.getImage();
            log.info("📸 카카오 프로필 이미지 URL: {}", kakaoImageUrl);

            try {
                URL url = new URL(kakaoImageUrl);
                String fileName = "kakao-profile.jpg";
                MockMultipartFile mockFile = new MockMultipartFile(
                        fileName,
                        fileName,
                        "image/jpeg",
                        url.openStream()
                );

                String uploadedUrl = imageService.saveImage(mockFile);
                user.setImage(uploadedUrl);
                userRepository.save(user);
            } catch (Exception e) {
                log.warn("❌ 프로필 이미지 S3 업로드 실패", e);
            }
        }

        // 최초 가입 시 userActive 설정 및 저장
        if (isNewUser) {
            user.setUserActive(UserActive.ACTIVE);
        }

        // 변경 사항 저장
        if (isNewUser || user.getImage() == null) {
            userRepository.save(user);
        }

        // 1. 매 로그인 시 AccessToken은 새로 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getRole()
        );

        // 쿠키 생성
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // HTTPS 배포 환경이라면 true
                .path("/")
                .sameSite("Strict")
                .maxAge(JwtTokenizer.ACCESS_TOKEN_EXPIRE_TIME / 1000) // 초 단위
                .build();
        response.addHeader("Set-Cookie", accessTokenCookie.toString());

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
