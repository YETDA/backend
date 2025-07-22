package com.funding.backend.domain.admin.controller;

import com.funding.backend.domain.admin.dto.response.UserCountDto;
import com.funding.backend.domain.admin.dto.response.UserInfoDto;
import com.funding.backend.domain.admin.dto.response.UserListDto;
import com.funding.backend.domain.admin.service.AdminUserManagingService;
import com.funding.backend.enums.UserActive;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/users/counts")
    @Operation(
            summary = "회원 수 조회",
            description = "(RoleType.USER만) 전체 회원 수, 활성 회원 수, 정지 회원 수를 조회합니다.")
    public ResponseEntity<ApiResponse<UserCountDto>> getUserCounts() {
        UserCountDto counts = adminUserManagingService.getUserCounts();
        return ResponseEntity.ok(ApiResponse.of(
                HttpStatus.OK.value(), "회원 수 조회 성공", counts
        ));
    }

    @GetMapping("/users")
    @Operation(
            summary = "회원 목록 조회 (페이징 + 정렬)",
            description = """
                        기본값: page=0, size=3, sort=createdAt,DESC ▶ 쿼리파라미터로  
                        • page (0부터 시작)  
                        • size (한 페이지 당 개수)  
                        • sort (필드명,정렬방향)  
                        를 자유롭게 지정할 수 있습니다.
                    """
    )
    public ResponseEntity<ApiResponse<Page<UserListDto>>> listUsers(
            @ParameterObject
            @PageableDefault(page = 0, size = 3,
                    sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        Page<UserListDto> page = adminUserManagingService.getUserList(pageable);
        return ResponseEntity.ok(ApiResponse.of(
                HttpStatus.OK.value(), "회원 목록 조회 성공", page
        ));
    }

    @GetMapping("/users/{userId}")
    @Operation(
            summary = "회원 상세 조회",
            description = "회원의 기본 정보를 조회합니다."
    )
    public ResponseEntity<ApiResponse<UserInfoDto>> getUserDetail(
            @PathVariable(name = "userId") Long userId
    ) {
        UserInfoDto dto = adminUserManagingService.getUserInfoDetail(userId);
        return ResponseEntity.ok(ApiResponse.of(
                HttpStatus.OK.value(), userId + "번 회원 상세 정보 조회 성공", dto
        ));
    }
}
