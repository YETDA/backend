package com.funding.backend.security.controller;


import com.funding.backend.security.jwt.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
@Tag(name = "토큰 생성 API")
public class TokenController {

    private final TokenService tokenService;


    @Operation(
            summary = "유저 역할 기반 Access Token 발급",
            description = "일반 사용자 권한으로 Access Token을 발급합니다. 이메일 주소를 기반으로 토큰 생성이 수행됩니다."
    )
    @PostMapping("/user")
    public ResponseEntity<Void> sendUserVerification() {
        tokenService.createTokenByUserRole();
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "관리자 역할 기반 Access Token 발급",
            description = "관리자 권한으로 Access Token을 발급합니다. 이메일 주소를 기반으로 토큰 생성이 수행됩니다."
    )
    @PostMapping("/admin")
    public ResponseEntity<Void> sendAdminVerification() {
        tokenService.createTokenByAdminRole();
        return ResponseEntity.ok().build();
    }





}
