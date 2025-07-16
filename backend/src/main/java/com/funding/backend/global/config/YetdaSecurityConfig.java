package com.funding.backend.global.config;

import com.funding.backend.security.jwt.JwtAuthFilter;
import com.funding.backend.security.oauth.CustomOAuth2UserService;
import com.funding.backend.security.oauth.handler.OAuth2LoginSuccessHandler;
import com.funding.backend.security.oauth.resolver.CustomAuthorizationRequestResolver;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class YetdaSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthorizationRequestResolver customAuthorizationRequestResolver;


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api/v1/token/**",
                                "/oauth2/**",
                                "/api/v1/user/logout",
                                "/login"
                        ).permitAll()

                        //프로젝트 (검색 포함됨)
                        .requestMatchers(HttpMethod.GET, "/api/v1/project/**").permitAll()

                        //구매 프로젝트 CRUD
                        .requestMatchers(HttpMethod.GET, "/api/v1/project/purchase/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/project/purchase/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/project/purchase/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/project/purchase/**").hasAnyRole("ADMIN", "USER")


                        //구매옵션
                        .requestMatchers(HttpMethod.GET, "/api/v1/purchaseOption/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/purchaseOption/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/purchaseOption/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/purchaseOption/**").hasAnyRole("ADMIN", "USER")

                        //유저
                        .requestMatchers(HttpMethod.PUT, "/api/v1/user/mypage/account/** ").hasAnyRole("ADMIN", "USER")


                        //공지사항
                        .requestMatchers(HttpMethod.GET, "/api/v1/notice/project/**").permitAll()

                        //좋아요
                        .requestMatchers(HttpMethod.GET, "/api/v1/like/project/**").permitAll()

                        //후원형
                        .requestMatchers(HttpMethod.GET, "/api/v1/donation/**").permitAll()

                        //리뷰
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()

                        //Q&A
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .authorizationEndpoint(
                                authorizationEndpoint ->
                                        authorizationEndpoint.authorizationRequestResolver(
                                                customAuthorizationRequestResolver)
                        )
                )

                .formLogin(form -> form.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(httpBasic -> httpBasic.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Unauthorized\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://yetda.kro.kr",
                "https://localhost:3000",
                "https://www.yetda.booktri.site", "https://www.yetfront.booktri.site"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(true); // 💡 쿠키 포함 허용 필수
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
