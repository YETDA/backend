package com.funding.backend.domain.report.controller;


import com.funding.backend.domain.report.dto.request.ReportRequestDto;
import com.funding.backend.domain.report.dto.response.ReportResponseDto;
import com.funding.backend.domain.report.service.ReportService;
import com.funding.backend.enums.ReportStatus;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/reports")
@Tag(name = "신고 관리", description = "신고 관련 CRUD 및 승인 관리")
public class ReportController {

    private final ReportService reportService;

    @Operation(summary = "내 신고 목록 조회", description = "본인이 작성한 신고 내역 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReportResponseDto>>> getMyReports(Pageable pageable) {
        Page<ReportResponseDto> response = reportService.getMyReports(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "내 신고 목록 조회 성공", response));
    }

    @Operation(summary = "신고 상세 조회", description = "신고자와 관리자가 상세조회 접근")
    @GetMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponseDto>> getReportDetail(@PathVariable Long reportId){

        ReportResponseDto response = reportService.getReportDetail(reportId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "신고 상세 조회 성공", response));
    }

    @Operation(summary = "신고 전체 조회", description = "전체 신고 내역 조회(관리자)")
    @GetMapping("/admin/report")
    public ResponseEntity<ApiResponse<Page<ReportResponseDto>>> getAllReports(@RequestParam(required = false)ReportStatus status, Pageable pageable){
        Page<ReportResponseDto> response = reportService.getAllReports(status, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "전체 신고 목록 조회 성공", response));
    }

    @Operation(summary = "프로젝트별 신고 목록", description = "해당 프로젝트에 대한 신고 목록(관리자)")
    @GetMapping("/admin/project/{projectId}")
    public ResponseEntity<ApiResponse<Page<ReportResponseDto>>> getProjectReports(@PathVariable Long projectId, Pageable pageable){
        Page<ReportResponseDto> response = reportService.getReportsByProject(projectId, pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "프로젝트별 신고 조회 성공", response));
    }

    @Operation(summary = "신고 등록", description = "신고 새로 등록하기")
    @PostMapping
    public ResponseEntity<ApiResponse<ReportResponseDto>> createReport(@RequestBody @Valid ReportRequestDto requestDto){
        ReportResponseDto response = reportService.createReport(requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "신고 등록 성공", response));
    }

    @Operation(summary = "신고 수정", description = "PENDING 상태의 신고를 수정")
    @PutMapping("/{reportId}")
    public ResponseEntity<ApiResponse<ReportResponseDto>> updateReport(@PathVariable Long reportId,
                                                                       @RequestBody @Valid ReportRequestDto requestDto){
        ReportResponseDto response = reportService.updateReport(reportId, requestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "신고 수정 성공", response));
    }

    @Operation(summary = "신고 삭제", description = "PENDING 상태의 신고를 작성자가 삭제")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(@PathVariable Long reportId){
        reportService.deleteReport(reportId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "신고 삭제 성공"));
    }

    @Operation(summary = "신고 승인", description = "관리자가 신고 승인 및 프로젝트 비공개")
    @PostMapping("/admin/{reportId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveReport(@PathVariable Long reportId){
        reportService.approveReport(reportId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "신고 승인 성공"));
    }

    @Operation(summary = "신고 반려", description = "관리자가 신고 반려")
    @PostMapping("/admin/{reportId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectReport(@PathVariable Long reportId){
        reportService.rejectReport(reportId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "신고 반려 성공"));
    }

}
