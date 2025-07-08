package com.funding.backend.security.jwt;

import com.funding.backend.domain.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenizer jwtTokenizer;
    private final UserRepository userRepository;

    public Long getUserIdFromAccessToken(HttpServletRequest request) {
        String token = extractToken(request);

        if (token == null) {
            throw new IllegalArgumentException("Token is missing");  // 토큰이 없으면 예외 처리
        }
        
        return jwtTokenizer.getUserIdFromAccessToken(token);
    }

    public String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // 쿠키에서 accessToken 찾기
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        throw new IllegalArgumentException("Access Token 없음");
    }
}