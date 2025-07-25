package com.funding.backend.security.filter;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.security.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class UserStatusCheckFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        Long userId = null;
        try {
            userId = tokenService.getUserIdFromAccessToken();
        } catch (Exception e) {
        }

        if (userId != null) {
            User user = userService.findUserById(userId);

            // 신고 3회 이상(STOP)인 경우
            if (user.getApprovedReportCount() >= 3) {
                String path = request.getRequestURI();

                // “로그인만” 허용할 로그인 관련 엔드포인트는 예외로 두고
                if (!path.startsWith("/oauth2/**")
                        && !path.startsWith("/login/oauth2/**")
                ) {
                    // 나머지 모두 403 차단
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"message\":\"신고된 사용자입니다. 계정 복구를 원하시면 이메일을 보내주세요.\"}");
                    return;
                }
            }
        }

        // 정지 계정이 아니거나, 예외 경로라면 다음 필터/컨트롤러로 진행
        chain.doFilter(request, response);
    }
}