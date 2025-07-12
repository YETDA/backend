package com.funding.backend.domain.admin.service;

import com.funding.backend.domain.project.dto.response.ReviewProjectResponseDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.RoleType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TokenService tokenService;
    private final UserService userService;
    private final ProjectService projectService;

    public boolean validAdmin() {
        Long userId = tokenService.getUserIdFromAccessToken();

        RoleType userRole = userService.getUserOrThrow(userId).getRole().getRole();

        if (userRole == null) {
            throw new BusinessLogicException(ExceptionCode.ADMIN_ROLE_REQUIRED);
        }

        return true;
    }

    public Page<ReviewProjectResponseDto> getAllUnderReviewProjects(Pageable pageable) {
        validAdmin();

        return projectService.findAllUnderReviewProjects(pageable);
    }

    public ReviewProjectResponseDto approveProject(Long projectId) {
        validAdmin();

        if (projectService.findProjectById(projectId).getProjectStatus() != ProjectStatus.UNDER_REVIEW) {
            throw new BusinessLogicException(ExceptionCode.PROJECT_CANNOT_BE_REVIEWED);
        }

        return projectService.updateProjectStatus(projectId, ProjectStatus.RECRUITING);
    }

    public ReviewProjectResponseDto rejectProject(Long projectId) {
        validAdmin();

        if (projectService.findProjectById(projectId).getProjectStatus() != ProjectStatus.UNDER_REVIEW) {
            throw new BusinessLogicException(ExceptionCode.PROJECT_CANNOT_BE_REVIEWED);
        }

        return projectService.updateProjectStatus(projectId, ProjectStatus.REJECTED);
    }
}
