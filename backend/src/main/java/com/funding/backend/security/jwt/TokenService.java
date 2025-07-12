package com.funding.backend.security.jwt;

import com.funding.backend.domain.role.repository.RoleRepository;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenizer jwtTokenizer;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    // 쿠키에서 AccessToken을 추출
    public String getAccessToken() {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new BusinessLogicException(ExceptionCode.ACCESS_TOKEN_NOT_FOUND);
    }

    // 쿠키에서 토큰을 꺼내 디코딩
    public Long getUserIdFromAccessToken() {
        String token = getAccessToken();
        return jwtTokenizer.getUserIdFromAccessToken(token);
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

    public void createTokenByUserRole() {
        User user = userService.findUserById(4L);
        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getName(),
                user.getRole().getRole());

        setCookie("accessToken", accessToken);
    }

    public void createTokenByAdminRole() {
        User user = userService.findUserById(4L);

        userRepository.save(user);
        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getEmail(), user.getName(),
                user.getRole().getRole());

        setCookie("accessToken", accessToken);
    }

    public void setCookie(String name, String value) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .maxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_TIME / 1000))
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}