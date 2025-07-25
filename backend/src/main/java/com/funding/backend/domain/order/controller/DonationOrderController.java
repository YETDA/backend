package com.funding.backend.domain.order.controller;

import com.funding.backend.domain.order.dto.request.DonationOrderRequestDto;
import com.funding.backend.domain.order.dto.response.DonationOrderResponseDto;
import com.funding.backend.domain.order.service.DonationOrderService;
import com.funding.backend.domain.project.dto.response.ProjectInfoResponseDto;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order/donation")
@Validated
@RequiredArgsConstructor
@Tag(name = "후원물 구매 관리 컨트롤러")
public class DonationOrderController {

    private final DonationOrderService donationOrderService;

    @PostMapping
    @Operation(
            summary = "후원 주문 생성",
            description = "후원 일괄 정산 전에 주문 정보를 생성합니다."
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<DonationOrderResponseDto>> createPurchaseOrder(
            @Valid @RequestBody DonationOrderRequestDto request
    ) {
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.CREATED.value(),
                        "후원 주문 생성 성공",
                        donationOrderService.createOrder(request)
                ));
    }

    @DeleteMapping
    @Operation(
            summary = "후원 주문 취소",
            description = "후원 주문을 취소합니다."
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Boolean>> cancelDonationOrder(
            @RequestParam("orderId") String orderId
    ) {
        return ResponseEntity.ok(
                ApiResponse.of(
                        HttpStatus.OK.value(),
                        "후원 주문 취소 성공",
                        donationOrderService.cancelOrder(orderId)
                ));
    }

    @GetMapping
    @Operation(
            summary = "사용자가 결제한 후원물 프로젝트 리스트 조회 ",
            description = "사용자가 결제한 후원물 프로젝트 리스트를 조회합니다. "
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Page<ProjectInfoResponseDto>>> getDonationProjectList(
            @ParameterObject Pageable pageable
    ) {
        Page<ProjectInfoResponseDto> response = donationOrderService.getDonationProjectList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "후원 프로젝트 리스트 조회 완료",response));
    }
}
