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
                                "/login",
                                "/login/oauth2/**",
                                "/auth/refresh",
                                "/api/v1/user/logout",
                                "/login/oauth2/**",
                                "/login/**"
                        ).permitAll()

                        //알림 요청
                        .requestMatchers(HttpMethod.GET, "/api/v1/alarm/stream").hasAnyRole("ADMIN", "USER")

                        //프로젝트 (검색 포함됨)
                        .requestMatchers(HttpMethod.GET, "/api/v1/project/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/project/purchase/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/project/donation/category/**").permitAll()

                        //알림
                        .requestMatchers(HttpMethod.POST, "/api/v1/alarm/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/alarm/stream/**").authenticated()

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
                        .requestMatchers(HttpMethod.POST, "/api/v1/purchaseOption/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/purchaseOption/**").hasAnyRole("ADMIN","USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/purchaseOption/**").hasAnyRole("ADMIN","USER")

                        //공지사항
                        .requestMatchers(HttpMethod.GET, "/api/v1/notice/project/**").permitAll()

                        //좋아요
                        .requestMatchers(HttpMethod.GET, "/api/v1/like/project/**").permitAll()

                        //후원 프로젝트 CRUD
                        .requestMatchers(HttpMethod.GET, "/api/v1/project/donation/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/project/donation/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/project/donation/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/project/donation/**").hasAnyRole("ADMIN", "USER")

                        //후원 로드맵(마일스톤)
                        .requestMatchers(HttpMethod.GET, "/api/v1/donationMilestone/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/donationMilestone/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/donationMilestone/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/donationMilestone/**").hasAnyRole("ADMIN", "USER")

                        //후원 리워드
                        .requestMatchers(HttpMethod.GET, "/api/v1/donationReward/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/donationReward/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/donationReward/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/donationReward/**").hasAnyRole("ADMIN", "USER")

                        //리뷰
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()

                        //Q&A
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews/**").permitAll()

                        //구매 주문서 생성
                        .requestMatchers(HttpMethod.POST,"/api/v1/order/purchase/**").hasAnyRole("ADMIN", "USER")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/**")
                        .authorizationEndpoint(endpoint ->
                                endpoint.authorizationRequestResolver(customAuthorizationRequestResolver)
                        )
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler((req, res, ex) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json;charset=UTF-8");
                            res.getWriter().write("{\"message\":\"" + ex.getMessage() + "\"}");
                        })
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
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://yetda.kro.kr"
                ,"https://yetdatest.kro.kr","https://yetdatest.kro.kr:3000", "https://localhost:3000"
                ,"https://www.yetda.booktri.site", "https://www.yetfront.booktri.site"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.addAllowedHeader("*");
        config.setAllowCredentials(true); // 💡 쿠키 포함 허용 필수
        config.setMaxAge(3600L);
        // SSE를 위한 추가 헤더 설정
        config.setExposedHeaders(List.of("Last-Event-ID", "Cache-Control", "Connection"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
