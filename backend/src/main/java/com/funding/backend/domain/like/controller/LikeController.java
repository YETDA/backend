package com.funding.backend.domain.like.controller;

import com.funding.backend.domain.like.service.LikeService;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Tag(name = "좋아요", description = "좋아요 관련 API 입니다.")
@RestController
@RequestMapping("api/v1/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(
            summary = "좋아요 토글",
            description = "프로젝트에 대한 좋아요를 추가하거나 취소합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "좋아요 토글 성공", likeService.toggleLike(projectId)));
    }

    @Operation(
            summary = "좋아요한 프로젝트 목록 조회",
            description = "사용자가 좋아요한 프로젝트 목록을 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/project")
    public ResponseEntity<ApiResponse<Page<ProjectResponseDto>>> getLikedProjects(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "좋아요한 프로젝트 조회 성공", likeService.getLikedProjects(pageable)));
    }

    @Operation(
            summary = "좋아요 여부 조회",
            description = "특정 프로젝트에 대해 사용자가 좋아요를 눌렀는지 여부를 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @GetMapping("/project/{projectId}/liked")
    public ResponseEntity<ApiResponse<Boolean>> isLikedByUser(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "좋아요 여부 조회 성공", likeService.isLikedByUser(projectId)));
    }

    @Operation(
            summary = "프로젝트 좋아요 수 조회",
            description = "특정 프로젝트에 대한 좋아요 수를 조회합니다."
    )
    @GetMapping("/project/{projectId}/count")
    public ResponseEntity<ApiResponse<Integer>> getLikeCountByProjectId(
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "좋아요 수 조회 성공", likeService.countLikesByProjectId(projectId)));
    }
}
