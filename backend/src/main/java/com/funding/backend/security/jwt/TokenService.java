package com.funding.backend.security.jwt;

import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenizer jwtTokenizer;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    // 요청에서 AccessToken을 추출
    public String getAccessToken() {
        // 1. Authorization 헤더 확인
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 2. 쿠키에서 accessToken 확인
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
    }

    // 요청에서 RefreshToken을 추출
    public String getRefreshToken() {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new BusinessLogicException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
    }

    // AccessToken에서 사용자 ID 추출
    public Long getUserIdFromAccessToken() {
        String token = getAccessToken();
        return jwtTokenizer.getUserIdFromAccessToken(token);
    }

    // AccessToken에서 사용자 Email 추출
    public String getEmailFromAccessToken() {
        String token = getAccessToken();
        return jwtTokenizer.getEmailFromAccessToken(token);
    }

    // 특정 쿠키 제거
    public void deleteCookie(String name) {
        ResponseCookie cookie = ResponseCookie.from(name, null)
                .path("/")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .maxAge(0) // 즉시 만료
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}