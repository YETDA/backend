package com.funding.backend.security.filter;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class UserActiveFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    public UserActiveFilter(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/oauth2/authorization")
                || path.startsWith("/login/oauth2/code");
//                || path.equals("/api/v1/users/reactivate");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            Long userId = tokenService.getUserIdFromAccessToken();
            User user = userService.findUserById(userId);
            // 정지 상태면 차단
            if (user.getUserActive() == UserActive.STOP) {
                throw new BusinessLogicException(ExceptionCode.USER_STOPPED);
            }
            // ACTIVE 이면 정상 진행
            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            throw new BusinessLogicException(ExceptionCode.INVALID_ACCESS_TOKEN);
        }
    }
}