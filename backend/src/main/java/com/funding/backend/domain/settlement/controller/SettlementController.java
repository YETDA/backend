package com.funding.backend.domain.settlement.controller;


import com.funding.backend.domain.settlement.dto.response.SettlementDetailListResponseDto;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailResponseDto;
import com.funding.backend.domain.settlement.dto.response.SettlementMonthlyTotalResponseDto;
import com.funding.backend.domain.settlement.service.SettlementService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement")
@Validated
@AllArgsConstructor
@Tag(name = "프로젝트 정산 관리 컨트롤러")
@Slf4j
public class SettlementController {

    private final SettlementService settlementService;

    //구매 프로젝트 정산 관리
    @GetMapping("/purchase/{projectId}")
    @Operation(
            summary = "구매형 프로젝트 정산 상세 조회",
            description = "사용자가 자신의 구매형 프로젝트에 대해 가장 최근 정산 정보를 조회합니다. 후원형 프로젝트는 제외됩니다."
    )
    public ResponseEntity<ApiResponse<SettlementDetailResponseDto>> getLatestPurchaseSettlementDetail(
            @PathVariable Long projectId
    ) {
        SettlementDetailResponseDto response = settlementService.getLatestPurchaseSettlementDetail(projectId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "정산 상세 조회 성공", response));
    }

    @GetMapping("/purchase/list")
    @Operation(
            summary = "사용자의 구매형 프로젝트 정산 리스트 조회",
            description = "사용자가 자신의 구매형 프로젝트에 대해 가장 최근 정산 정보 리스트를 조회합니다. 후원형 프로젝트는 제외됩니다."
    )
    public ResponseEntity<ApiResponse<Page<SettlementDetailListResponseDto>>> getMyPurchaseProjectSettlementList(
            @ParameterObject Pageable pageable
    ) {
        Page<SettlementDetailListResponseDto> response = settlementService.getPurchaseSettlementDetailsListByUser(pageable);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "정산 리스트 조회 성공", response));
    }


    @GetMapping("/purchase/total")
    @Operation(
            summary = "사용자의 특정 월 구매형 프로젝트 정산 금액 조회",
            description = "사용자가 해당 연/월에 정산된 구매형 프로젝트 총 금액을 조회합니다. 후원형은 제외됩니다."
    )
    public ResponseEntity<ApiResponse<SettlementMonthlyTotalResponseDto>> getMyMonthlyPurchaseSettlementTotal(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
    ) {
        SettlementMonthlyTotalResponseDto response = settlementService.getPurchaseSettlementTotalByMonth(yearMonth);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "월별 정산 금액 조회 성공", response));
    }



}
