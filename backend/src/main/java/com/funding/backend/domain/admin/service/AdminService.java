package com.funding.backend.domain.admin.service;

import com.funding.backend.domain.project.dto.response.AuditProjectResponseDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.enums.RoleType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.security.jwt.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TokenService tokenService;
    private final UserService userService;
    private final ProjectService projectService;

    public boolean validAdmin() {
        Long userId = tokenService.getUserIdFromAccessToken();

        RoleType userRole = userService.getUserOrThrow(userId).getRole().getRole();

        if (userRole != RoleType.ADMIN) {
            throw new BusinessLogicException(ExceptionCode.ADMIN_ROLE_REQUIRED);
        }

        return true;
    }

    public Page<AuditProjectResponseDto> getAllProjectsByTypsAndStatus(ProjectType type, List<ProjectStatus> statuses, Pageable pageable) {
        validAdmin();

        return projectService.findProjectsByTypeAndStatus(type, statuses, pageable);
    }

    public AuditProjectResponseDto approveProject(Long projectId) {
        validAdmin();

        if (projectService.findProjectById(projectId).getProjectStatus() != ProjectStatus.UNDER_AUDIT) {
            throw new BusinessLogicException(ExceptionCode.PROJECT_CANNOT_BE_AUDITED);
        }

        return projectService.updateProjectStatus(projectId, ProjectStatus.RECRUITING);
    }

    public AuditProjectResponseDto rejectProject(Long projectId) {
        validAdmin();

        if (projectService.findProjectById(projectId).getProjectStatus() != ProjectStatus.UNDER_AUDIT) {
            throw new BusinessLogicException(ExceptionCode.PROJECT_CANNOT_BE_AUDITED);
        }

        return projectService.updateProjectStatus(projectId, ProjectStatus.REJECTED);
    }

    @Transactional
    public void approveAllProjects() {
        validAdmin();
        projectService.updateAllProjectStatus(ProjectStatus.UNDER_AUDIT, ProjectStatus.RECRUITING);
    }

    @Transactional
    public void rejectAllProjects() {
        validAdmin();
        projectService.updateAllProjectStatus(ProjectStatus.UNDER_AUDIT, ProjectStatus.REJECTED);
    }
}
