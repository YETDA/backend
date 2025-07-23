package com.funding.backend.domain.admin.service;

import com.funding.backend.domain.admin.dto.response.UserCountDto;
import com.funding.backend.domain.admin.dto.response.UserInfoDto;
import com.funding.backend.domain.admin.dto.response.UserListDto;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.settlement.service.SettlementService;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.repository.UserRepository;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.RoleType;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserManagingService {

    private final TokenService tokenService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ProjectService projectService;
    private final OrderService orderService;
    private final SettlementService settlementService;

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

    public UserCountDto getUserCounts() {
        validAdmin();
        RoleType filterRole = RoleType.USER;

        long totalUsers = userRepository.countByRole_Role(filterRole);
        long activeUsers = userRepository.countByRole_RoleAndUserActive(filterRole, UserActive.ACTIVE);
        long stopUsers = userRepository.countByRole_RoleAndUserActive(filterRole, UserActive.STOP);

        return new UserCountDto(totalUsers, activeUsers, stopUsers);
    }

    public Page<UserListDto> getUserList(Pageable pageable) {
        validAdmin();
        return userRepository.findByRoleWithStats(RoleType.USER, pageable);
    }

    public UserInfoDto getUserInfoDetail(Long targetUserId) {
        validAdmin();
        User u = userService.findUserById(targetUserId);

        return new UserInfoDto(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getSsoProvider(),
                u.getCreatedAt(),
                u.getApprovedReportCount(),
                u.getUserActive(),
                u.getImage()
        );
    }


}
