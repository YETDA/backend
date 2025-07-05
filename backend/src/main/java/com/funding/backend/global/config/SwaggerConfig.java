package com.funding.backend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "기술 기반 크라우드 펀딩 플랫폼",
                version = "1.0",
                description = "기술 기반 크라우드 펀딩 플랫폼을 위한 API 문서"
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name="bearerAuth",
        type= SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SwaggerConfig {
}