package com.funding.backend.security.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
