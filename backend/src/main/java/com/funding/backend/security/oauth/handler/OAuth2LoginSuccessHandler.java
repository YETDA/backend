package com.funding.backend.security.oauth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.utils.s3.ByteArrayMultipartFile;
import com.funding.backend.global.utils.s3.ImageService;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.RefreshTokenService;
import com.funding.backend.security.jwt.TokenService;
import com.funding.backend.security.oauth.model.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final ImageService imageService;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        log.info("✅ OAuth2LoginSuccessHandler 호출됨!");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        // 최초 회원가입 판단
        boolean isNewUser = user.getUserActive() == null;

        // 카카오 프로필 이미지 s3 업로드
        String provider = user.getSsoProvider();  // User 객체에 저장된 SSO provider
        if ("KAKAO".equalsIgnoreCase(provider)
                && user.getImage() != null
                && user.getImage().contains("k.kakaocdn.net")) {
            String kakaoImageUrl = user.getImage();
            log.info("📸 카카오 프로필 이미지 URL: {}", kakaoImageUrl);
            try {
                URL url = new URL(kakaoImageUrl);
                byte[] imageBytes = url.openStream().readAllBytes();
                String fileName = "kakao-profile/" + user.getId() + ".jpg";

                MultipartFile multipartFile = new ByteArrayMultipartFile(
                        fileName,          // name
                        fileName,          // original filename
                        "image/jpeg",      // content type
                        imageBytes         // content
                );

                String uploadedUrl = imageService.saveImage(multipartFile);
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

        // AccessToken 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().getRole()
        );

        // Refresh Token은 Redis에 있으면 재사용, 없으면 발급 및 저장
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

        // 쿠키 생성
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(true) // 로컬 HTTP 개발 시 false. HTTPS 프로덕션에선 true
                .path("/")
                .sameSite("None")
                .maxAge(JwtTokenizer.ACCESS_TOKEN_EXPIRE_TIME / 1000) // 초 단위
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(JwtTokenizer.REFRESH_TOKEN_EXPIRE_TIME / 1000)
                .build();

        log.info("→ Setting AccessCookie: {}", accessTokenCookie);
        log.info("→ Setting RefreshCookie: {}", refreshTokenCookie);
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // 리다이렉트
        String redirectUrl = request.getParameter("state");
        log.info("리다이렉트 대상: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }
}