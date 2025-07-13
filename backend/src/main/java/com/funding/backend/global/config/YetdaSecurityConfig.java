package com.funding.backend.global.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.funding.backend.security.jwt.JwtAuthFilter;
import com.funding.backend.security.oauth.CustomOAuth2UserService;
import com.funding.backend.security.oauth.handler.OAuth2LoginSuccessHandler;
import com.funding.backend.security.oauth.resolver.CustomAuthorizationRequestResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests( auth -> auth
                        // GET 요청 허용
                        .requestMatchers(HttpMethod.GET, PermitUrl.GET_URLS).permitAll()
                        // POST 요청 허용
                        .requestMatchers(HttpMethod.POST, PermitUrl.POST_URLS).permitAll()
                        // PUT 요청 허용
                        .requestMatchers(HttpMethod.PUT, PermitUrl.PUT_URLS).permitAll()
                        // PATCH 요청 허용
                        .requestMatchers(HttpMethod.PATCH, PermitUrl.PATCH_URLS).permitAll()
                        // DELETE 요청 허용
                        .requestMatchers(HttpMethod.DELETE, PermitUrl.DELETE_URLS).permitAll()
                        // 모든 요청 허용 (ALL_URLS)
                        .requestMatchers(PermitUrl.ALL_URLS).permitAll()

                        .requestMatchers(HttpMethod.OPTIONS, PermitUrl.OPTIONS_URLS).permitAll()
                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
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
                .httpBasic(httpBasic -> httpBasic.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // 실제 도메인 추후 추가
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
