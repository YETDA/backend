package com.funding.backend.domain.admin.controller;

import com.funding.backend.domain.admin.service.AdminUserManagingService;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 전용 API - 유저 관리", description = "관리자만 사용 가능한 기능입니다.")
public class AdminUserManagingController {

    private final AdminUserManagingService adminUserManagingService;

    @PatchMapping("/users/{userId}/status")
    @Operation(
            summary = "회원(유저) 상태 변경",
            description = "관리자가 유저의 ACTIVE/STOP 상태를 변경합니다.")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "status") UserActive status
    ) {
        adminUserManagingService.changeUserStatus(userId, status);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(),
                userId + "번 회원의 상태를 " + status + "(으)로 변경했습니다."));
    }
}
