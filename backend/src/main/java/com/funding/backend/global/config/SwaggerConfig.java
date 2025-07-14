package com.funding.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "기술 기반 크라우드 펀딩 플랫폼",
                version = "1.0",
                description = "기술 기반 크라우드 펀딩 플랫폼을 위한 API 문서"
        ),
        security = {
                @SecurityRequirement(name = "bearerAuth"),
                @SecurityRequirement(name = "kakaoOAuth")
        },
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 개발 서버"),
                @Server(url = "https://localhost", description = "로컬 Nginx 서버"),
                @Server(url = "https://yetda.kro.kr", description = "배포 서버")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@SecurityScheme(
        name = "kakaoOAuth", // Swagger에서 선택할 이름
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows(
                authorizationCode = @OAuthFlow(
                        authorizationUrl = "https://kauth.kakao.com/oauth/authorize", // 카카오 인증 URL
                        tokenUrl = "https://kauth.kakao.com/oauth/token",             // 카카오 토큰 발급 URL
                        scopes = {
                                @OAuthScope(name = "profile_nickname", description = "사용자 닉네임"),
                                @OAuthScope(name = "account_email", description = "사용자 이메일")
                        }
                )
        )
)
public class SwaggerConfig {
}
