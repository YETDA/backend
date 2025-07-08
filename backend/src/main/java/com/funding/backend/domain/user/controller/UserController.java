package com.funding.backend.domain.user.controller;

import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.security.jwt.RefreshTokenService;
import com.funding.backend.security.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(HttpServletRequest request) {
        Long userId = tokenService.getUserIdFromAccessToken(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        UserInfoResponse response = UserInfoResponse.from(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        // 1. accessToken에서 userId 추출
        Long userId = tokenService.getUserIdFromAccessToken(request);

        // 2. Redis에서 refreshToken 삭제
        refreshTokenService.deleteRefreshToken(userId);

        // 3. accessToken 쿠키 삭제
        tokenService.deleteCookie("accessToken", response);

        return ResponseEntity.ok().build();
    }
}