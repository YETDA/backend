package com.funding.backend.security.controller;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.RefreshTokenService;
import com.funding.backend.security.jwt.dto.request.TokenReissueRequest;
import com.funding.backend.security.jwt.dto.response.TokenReissueResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody TokenReissueRequest request) {
        Long userId = request.getUserId();
        String refreshToken = request.getRefreshToken();

        // 1. Redis에 저장된 Refresh Token과 비교
        if (!refreshTokenService.isValid(userId, refreshToken)) {
            return ResponseEntity.status(401).body("Refresh Token이 유효하지 않습니다.");
        }

        // 2. Refresh Token 복호화 및 만료 검사
        Claims claims;
        try {
            claims = jwtTokenizer.parseRefreshToken(refreshToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Refresh Token 만료 또는 오류");
        }

        // 3. 사용자 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        // 4. 새 Access Token 생성
        String newAccessToken = jwtTokenizer.createAccessToken(
                user.getId(), user.getEmail(), user.getName(), user.getRole().getRole());

        return ResponseEntity.ok(new TokenReissueResponse(newAccessToken));
    }
}