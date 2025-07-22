package com.funding.backend.domain.admin.service;

import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.RoleType;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserManagingService {

    private final TokenService tokenService;
    private final UserService userService;

    public boolean validAdmin() {
        Long userId = tokenService.getUserIdFromAccessToken();

        RoleType userRole = userService.getUserOrThrow(userId).getRole().getRole();

        if (userRole != RoleType.ADMIN) {
            throw new BusinessLogicException(ExceptionCode.ADMIN_ROLE_REQUIRED);
        }

        return true;
    }

    @Transactional
    public void changeUserStatus(Long targetUserId, UserActive newStatus) {
        validAdmin();
        User target = userService.findUserById(targetUserId);
        target.setUserActive(newStatus);
    }
}
