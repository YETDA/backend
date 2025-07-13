package com.funding.backend.domain.admin.controller;

import com.funding.backend.domain.admin.service.AdminService;
import com.funding.backend.domain.project.dto.response.AuditProjectResponseDto;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.http.ResponseEntity;

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
    public ResponseEntity<ApiResponse<AuditProjectResponseDto>> approveProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 승인을 성공했습니다", adminService.approveProject(projectId)));
    }

    @PostMapping("/project/{projectId}/reject")
    @Operation(
            summary = "프로젝트 반려",
            description = "프로젝트를 REJECTED 상태로 반려합니다."
    )
    public ResponseEntity<ApiResponse<AuditProjectResponseDto>> rejectProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 반려를 성공했습니다", adminService.rejectProject(projectId)));
    }

    @GetMapping("/project")
    @Operation(
            summary = "프로젝트 목록 조회",
            description = "특정 타입과 상태의 프로젝트 목록을 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<AuditProjectResponseDto>>> getAllProjectsByTypeAndStatus(
            @RequestParam ProjectType type,
            @RequestParam List<ProjectStatus> statuses,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "설정한 조건의 프로젝트 조회에 성공했습니다.", adminService.getAllProjectsByTypsAndStatus(type, statuses, pageable)));
    }

    @PostMapping("/project/approve-all")
    @Operation(
            summary = "프로젝트 일괄 승인",
            description = "심사중인 모든 프로젝트를 승인합니다."
    )
    public ResponseEntity<ApiResponse<String>> approveAllProjects() {
        adminService.approveAllProjects();
        return  ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "심사중인 프로젝트 일괄 승인에 성공했습니다.", null));
    }

    @PostMapping("/project/reject-all")
    @Operation(
            summary = "프로젝트 일괄 반려",
            description = "심사중인 모든 프로젝트를 반려합니다."
    )
    public ResponseEntity<ApiResponse<String>> rejectAllProjects() {
        adminService.rejectAllProjects();
        return  ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "심사중인 프로젝트 일괄 반려에 성공했습니다.", null));
    }
}
