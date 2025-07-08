package com.funding.backend.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {  // 공통 소셜 로그인 컨트롤러

    @GetMapping("/kakao/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("카카오 로그인 성공 테스트");
    }
}