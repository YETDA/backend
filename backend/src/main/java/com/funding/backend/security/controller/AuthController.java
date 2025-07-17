package com.funding.backend.security.controller;

import com.funding.backend.enums.RoleType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.JwtTokenizer;
import com.funding.backend.security.jwt.TokenService;
import com.funding.backend.security.jwt.dto.request.TokenReissueRequest;
import com.funding.backend.security.jwt.dto.response.TokenReissueResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
    private final TokenService tokenService;

    @Autowired
    @Qualifier("customStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;


    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/refresh")
    public void refresh(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String refreshToken = tokenService.getRefreshToken();
        log.info("✅ 전달받은 리프레시 토큰: {}", refreshToken);

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
        Object storedToken = redisTemplate.opsForValue().get(key);
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

        // 쿠키에 새 토큰 세팅
        //tokenService.setCookie("refreshToken", newRefreshToken);
        //tokenService.setCookie("accessToken", newAccessToken);

        String redirectUrl = request.getParameter("state");
        String redirectWithToken = redirectUrl + "?token=" + newAccessToken;
        log.info("!!!!!! redirect - url :::: " + redirectUrl);
        response.sendRedirect(redirectWithToken);
    }


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