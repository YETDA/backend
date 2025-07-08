package com.funding.backend.security.jwt.dto.request;

import lombok.Getter;

@Getter
public class TokenReissueRequest {
    private Long userId;
    private String refreshToken;
}