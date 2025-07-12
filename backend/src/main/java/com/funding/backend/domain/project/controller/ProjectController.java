package com.funding.backend.domain.project.controller;


import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.domain.project.dto.response.PopularProjectResponseDto;
import com.funding.backend.domain.project.dto.response.ReviewProjectResponseDto;
import com.funding.backend.domain.project.dto.response.ProjectSearchResponseDto;
import com.funding.backend.domain.project.service.ProjectService;
import com.funding.backend.enums.PopularProjectSortType;
import com.funding.backend.enums.ProjectTypeFilter;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/project")
@Validated
@AllArgsConstructor
@Tag(name = "구매 주문 API", description = "구매형 프로젝트의 주문/결제 요청을 처리합니다.")
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 상세 조회",
            description = "구매용, 후원용에 따라 응답 형식이 달라집니다."
    )
    public ResponseEntity<ApiResponse<ProjectResponseDto>> getProjectDetail(@PathVariable Long projectId) {
        ProjectResponseDto response = projectService.getProjectDetail(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 상세 조회 성공", response));
    }


    @DeleteMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 삭제",
            description = "프로젝트를 삭제합니다. 구매형/후원형에 관계없이 공통으로 삭제됩니다."
    )
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 삭제 성공"));
    }

    @GetMapping("/popular")
    @Operation(
            summary = "인기 프로젝트 조회",
            description = "프로젝트 타입과 정렬 기준에 따라 인기 프로젝트를 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<PopularProjectResponseDto>>> getPopularProjects(
            @RequestParam ProjectTypeFilter projectType,
            @RequestParam PopularProjectSortType sortType,
            Pageable pageable
    ) {
        Page<PopularProjectResponseDto> response = projectService.getPopularProjects(projectType, sortType, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "인기 프로젝트 조회 성공", response));
    }

    @GetMapping("under-review")
    @Operation(
            summary = "심사 중인 프로젝트 조회",
            description = "심사 중인 프로젝트를 조회합니다."
    )
    public ResponseEntity<ApiResponse<Page<ReviewProjectResponseDto>>> findAllUnderReviewProjects(Pageable pageable) {
        Page<ReviewProjectResponseDto> response = projectService.findAllUnderReviewProjects(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "심사 중인 프로젝트 조회 성공", response));
    }

    @Operation(summary = "프로젝트 검색 기능", description = "두 글자 이상 포함된 프로젝트 제목 검색")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ProjectSearchResponseDto>>> searchProject(
            @RequestParam String keyword, Pageable pageable) {

        Page<ProjectSearchResponseDto> response = projectService.searchProjectsByTitle(keyword.trim(), pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트 검색 성공", response));
    }



}
