package com.funding.backend.domain.notice.controller;

import com.funding.backend.domain.notice.dto.request.NoticeCreateRequestDto;
import com.funding.backend.domain.notice.dto.request.NoticeUpdateRequestDto;
import com.funding.backend.domain.notice.dto.response.NoticeReseponseDto;
import com.funding.backend.domain.notice.service.NoticeService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Tag(name = "공지사항 관리 API", description = "공지사항 관련 API 입니다.")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(
            summary = "공지사항 생성",
            description = "프로젝트 생성자가 공지사항을 생성합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping
    public ResponseEntity<ApiResponse<NoticeReseponseDto>> createNotice(
            @RequestBody @Valid NoticeCreateRequestDto noticeCreateRequestDto) {

        NoticeReseponseDto noticeResponse = noticeService.createNotice(noticeCreateRequestDto);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.CREATED.value(), "공지사항 생성 성공", noticeResponse));
    }

    @Operation(
            summary = "공지사항 수정",
            description = "프로젝트 생성자가 공지사항을 수정합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeReseponseDto>> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody @Valid NoticeUpdateRequestDto noticeUpdateRequestDto) {

        NoticeReseponseDto noticeResponse = noticeService.updateNotice(noticeId, noticeUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "공지사항 수정 성공", noticeResponse));
    }

    @Operation(
            summary = "공지사항 삭제",
            description = "프로젝트 생성자가 공지사항을 삭제합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            @PathVariable Long noticeId) {

        noticeService.deleteNotice(noticeId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "공지사항 삭제 성공"));
    }

    @Operation(
            summary = "공지사항 단건 조회",
            description = "특정 공지사항을 조회합니다."
    )
    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeReseponseDto>> getNoticeById(
            @PathVariable Long noticeId) {

        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "공지사항 수정 성공", new NoticeReseponseDto(noticeService.findNoticeById(noticeId))));
    }

    @Operation(
            summary = "프로젝트의 공지사항 전체 조회",
            description = "특정 프로젝트의 모든 공지사항을 조회합니다."
    )
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<Page<NoticeReseponseDto>>> getNoticesByProjectId(
            @PathVariable Long projectId,
            @ParameterObject Pageable pageable) {

        Page<NoticeReseponseDto> noticeResponses = noticeService.findNoticesByProjectId(projectId, pageable);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "공지사항 전체 조회 성공", noticeResponses));
    }
}
