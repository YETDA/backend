package com.funding.backend.domain.like.controller;

import com.funding.backend.domain.like.service.LikeService;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Tag(name = "좋아요", description = "좋아요 관련 API 입니다.")
@RestController
@RequestMapping("api/v1/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    // TODO: 추후 회원 기능 개발 시 주석 해제
    @Operation(
            summary = "좋아요 토글",
            description = "프로젝트에 대한 좋아요를 추가하거나 취소합니다."
//            security = @SecurityRequirement(name = "JWT")
    )
    @PostMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Boolean>> toggleLike(
            /* Long loginUserId, */
            @PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "좋아요 토글 성공", likeService.toggleLike(/* loginUserId, */projectId)));
    }

    // TODO: 추후 회원 기능 개발 시 주석 해제
    @Operation(
            summary = "좋아요한 프로젝트 목록 조회",
            description = "사용자가 좋아요한 프로젝트 목록을 조회합니다."
//            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/project")
    public ResponseEntity<ApiResponse<Page<ProjectResponseDto>>> getLikedProjects(/* Long loginUserId, */ Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "", likeService.getLikedProjects(/* loginUserId, */ pageable)));
    }
}
