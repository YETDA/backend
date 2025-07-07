package com.funding.backend.domain.notice.controller;

import com.funding.backend.domain.notice.dto.request.NoticeCreateRequestDto;
import com.funding.backend.domain.notice.dto.request.NoticeUpdateRequestDto;
import com.funding.backend.domain.notice.dto.response.NoticeReseponseDto;
import com.funding.backend.domain.notice.service.NoticeService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notice")
@RequiredArgsConstructor
@Tag(name = "공지사항 관리 API", description = "공지사항 관련 API 입니다.")
public class NoticeController {

    private final NoticeService noticeService;

    // TODO: 추후 회원 기능 개발 시 주석 해제
    @Operation(
            summary = "공지사항 생성",
            description = "프로젝트 생성자가 공지사항을 생성합니다."
//            security = @SecurityRequirement(name = "JWT")
    )
    @PostMapping("/{projectId}")
    public ResponseEntity<ApiResponse<NoticeReseponseDto>> createNotice(
            /* Long loginUserId, */
            @PathVariable Long projectId,
            @RequestBody NoticeCreateRequestDto noticeCreateRequestDto) {

        NoticeReseponseDto noticeResponse = noticeService.createNotice(/* loginUserId, */projectId, noticeCreateRequestDto);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.CREATED.value(), "공지사항 생성 성공", noticeResponse));
    }

    // TODO: 추후 회원 기능 개발 시 주석 해제
    @Operation(
            summary = "공지사항 수정",
            description = "프로젝트 생성자가 공지사항을 수정합니다."
//            security = @SecurityRequirement(name = "JWT")
    )
    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<NoticeReseponseDto>> updateNotice(
            /* Long loginUserId, */
            @PathVariable Long noticeId,
            @RequestBody NoticeUpdateRequestDto noticeUpdateRequestDto) {

        NoticeReseponseDto noticeResponse = noticeService.updateNotice(/* loginUserId, */noticeId, noticeUpdateRequestDto);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "공지사항 수정 성공", noticeResponse));
    }

    // TODO: 추후 회원 기능 개발 시 주석 해제
    @Operation(
            summary = "공지사항 삭제",
            description = "프로젝트 생성자가 공지사항을 삭제합니다."
    )
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(
            /* Long loginUserId, */
            @PathVariable Long noticeId) {

        noticeService.deleteNotice(/* loginUserId, */noticeId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "공지사항 삭제 성공"));
    }
}
