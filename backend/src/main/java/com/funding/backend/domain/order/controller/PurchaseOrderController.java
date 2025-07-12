package com.funding.backend.domain.order.controller;


import com.funding.backend.domain.order.dto.request.PurchaseOrderRequestDto;
import com.funding.backend.domain.order.dto.response.PurchaseFileResponseDto;
import com.funding.backend.domain.order.dto.response.PurchaseOrderResponseDto;
import com.funding.backend.domain.order.service.PurchaseOrderService;
import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order/purchase")
@Validated
@AllArgsConstructor
@Tag(name = "창작물 구매 관리 컨트롤러")
@Slf4j
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @Operation(
            summary = "구매 주문 생성",
            description = "결제 전에 주문 정보를 생성합니다."
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> createPurchaseOrder(
            @Valid  @RequestBody PurchaseOrderRequestDto request
    ) {
        PurchaseOrderResponseDto response = purchaseOrderService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "구매 주문 생성 성공",response));
    }

    @GetMapping
    @Operation(
            summary = "사용자가 결제한 창작물 프로젝트 리스트 조회 ",
            description = "사용자가 결제한 창작물 프로젝트 리스트를 조회합니다. "
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<Page<ProjectResponseDto>>> getPurchaseProjectList(
            @ParameterObject Pageable pageable
    ) {
        Page<ProjectResponseDto> response = purchaseOrderService.getPurchaseProjectList(pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "결제 프로젝트 리스트 조회 완료",response));
    }

    @GetMapping("/{purchaseOptionId}")
    @Operation(
            summary = "결제된 창작물 파일 조회",
            description = "현재 로그인한 사용자가 결제한 창작물(파일)을 조회합니다."
    )
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ApiResponse<PurchaseFileResponseDto>> getPurchasedProjectFiles(
            @PathVariable("purchaseOptionId") Long purchaseOptionId
    ) {
        PurchaseFileResponseDto response = purchaseOrderService.getUserPurchasedFile(purchaseOptionId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.of(HttpStatus.OK.value(), "결제 프로젝트 리스트 조회 완료",response));
    }





}
