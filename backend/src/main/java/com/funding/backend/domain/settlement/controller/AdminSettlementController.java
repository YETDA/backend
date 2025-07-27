package com.funding.backend.domain.settlement.controller;


import com.funding.backend.domain.settlement.dto.response.SettlementMonthlyTotalAdminResponseDto;
import com.funding.backend.domain.settlement.dto.response.SettlementProjectSummaryAdminResponseDto;
import com.funding.backend.domain.settlement.service.SettlementService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.YearMonth;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement/admin")
@Validated
@AllArgsConstructor
@Tag(name = "어드민 프로젝트 정산 관리 컨트롤러")
@Slf4j
public class AdminSettlementController {

    private final SettlementService settlementService;

    //달 정산 금액
    @GetMapping("/purchase/summary")
    @Operation(
            summary = "모든 구매형 프로젝트 정산 예정 금액 요약 조회 (관리자)",
            description = "관리자가 특정 연월에 대한 모든 구매형 프로젝트의 정산 예정 금액(총 주문 금액, 수수료, 지급 금액)을 조회합니다."
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<SettlementMonthlyTotalAdminResponseDto>> getMonthlyPurchaseSettlementSummary(
            @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        SettlementMonthlyTotalAdminResponseDto response = settlementService.getMonthlyPurchaseSettlementSummary(yearMonth);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "월별 정산 요약 조회 성공", response));
    }


//    전체 프로젝트 정산 현황
    @GetMapping("/purchase/summary/list")
    @Operation(
            summary = "모든 구매형 프로젝트 정산 현황 목록 조회 (관리자)",
            description = "관리자가 특정 연월에 대해 창작자별 프로젝트 정산 현황 리스트를 조회합니다."
    )
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<SettlementProjectSummaryAdminResponseDto>>> getMonthlyPurchaseSettlementSummaryList(
            @RequestParam("yearMonth") @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth,
            @ParameterObject Pageable pageable
    ) {
        Page<SettlementProjectSummaryAdminResponseDto> response = settlementService.getMonthlyPurchaseSettlementSummaryListForAdmin(yearMonth, pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "정산 현황 리스트 조회 성공", response));
    }





}
