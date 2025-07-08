package com.funding.backend.domain.user.controller;

import com.funding.backend.domain.user.dto.response.UserInfoResponse;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.security.jwt.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final TokenService tokenService;
    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(HttpServletRequest request) {
        Long userId = tokenService.getUserIdFromAccessToken(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        UserInfoResponse response = UserInfoResponse.from(user);
        return ResponseEntity.ok(response);
    }
}