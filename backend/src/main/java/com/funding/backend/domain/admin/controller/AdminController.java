package com.funding.backend.domain.admin.controller;

import com.funding.backend.domain.admin.service.AdminService;
import com.funding.backend.domain.project.dto.response.ReviewProjectResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 전용 API", description = "관리자만 사용 가능한 기능입니다.")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/project/{projectId}/approve")
    @Operation(
            summary = "프로젝트 승인",
            description = "프로젝트를 RECRUITING 상태로 승인합니다."
    )
    public ReviewProjectResponseDto approveProject(@PathVariable Long projectId) {
        return adminService.approveProject(projectId);
    }

    @PostMapping("/project/{projectId}/reject")
    @Operation(
            summary = "프로젝트 거절",
            description = "프로젝트를 REJECTED 상태로 거절합니다."
    )
    public ReviewProjectResponseDto rejectProject(@PathVariable Long projectId) {
        return adminService.rejectProject(projectId);
    }

    @GetMapping("/project/under-review")
    @Operation(
            summary = "심사 중인 프로젝트 조회",
            description = "모든 심사가 필요한 프로젝트를 조회합니다. (UNDER_REVIEW, REJECTED)"
    )
    public Page<ReviewProjectResponseDto> getAllUnderReviewProjects(@ParameterObject Pageable pageable) {
        return adminService.getAllUnderReviewProjects(pageable);
    }
}
