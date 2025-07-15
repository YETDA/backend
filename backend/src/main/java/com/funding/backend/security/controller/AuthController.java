package com.funding.backend.security.controller;

import com.funding.backend.enums.RoleType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.dto.request.TokenReissueRequest;
import com.funding.backend.security.jwt.dto.response.TokenReissueResponse;
import io.jsonwebtoken.Claims;
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

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/reissue")
    public ResponseEntity<TokenReissueResponse> reissue(@RequestBody TokenReissueRequest request) {
        String refreshToken = request.getRefreshToken();
        log.info("✅ 전달된 리프레시 토큰: {}", refreshToken);

        if (refreshToken == null || refreshToken.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }

        if (!jwtTokenizer.validateRefreshToken(refreshToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf(claims.get("userId").toString());

        String key = "refreshToken:" + userId;
        String storedToken = (String) redisTemplate.opsForValue().get(key);
        log.info("✅ Redis 저장된 토큰: {}", storedToken);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
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