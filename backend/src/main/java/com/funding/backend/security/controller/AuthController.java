package com.funding.backend.security.controller;

import com.funding.backend.enums.RoleType;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.dto.request.TokenReissueRequest;
import com.funding.backend.security.jwt.dto.response.TokenReissueResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JwtTokenizer jwtTokenizer;

    @Qualifier("redisTemplate") // 둘 중 하나 명시
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponse> reissue(@RequestBody TokenReissueRequest request) {
        String refreshToken = request.getRefreshToken();
        log.info("✅ 전달된 리프레시 토큰: {}", refreshToken);

        // Bearer 제거
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (!jwtTokenizer.validateRefreshToken(refreshToken)) {
            throw new JwtException("Refresh Token이 유효하지 않습니다.");
        }

        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf(claims.get("userId").toString());

        String key = "refreshToken:" + userId;
        String storedToken = redisTemplate.opsForValue().get(key);
        log.info("✅ Redis 저장된 토큰: {}", storedToken);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new JwtException("Refresh Token이 유효하지 않습니다.");
        }

        String email = claims.getSubject();
        String name = claims.get("username").toString();
        RoleType role = RoleType.valueOf(claims.get("role").toString());

        String newAccessToken = jwtTokenizer.createAccessToken(userId, email, name, role);
        String newRefreshToken = jwtTokenizer.createRefreshToken(userId, email, name, role);

        // Redis 갱신
        redisTemplate.opsForValue().set(key, newRefreshToken);

        TokenReissueResponse response = new TokenReissueResponse(newAccessToken, newRefreshToken);
        return ResponseEntity.ok(response);
    }
}