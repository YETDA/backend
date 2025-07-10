package com.funding.backend.domain.order.controller;


import com.funding.backend.domain.order.dto.request.PurchaseOrderRequestDto;
import com.funding.backend.domain.order.service.PurchaseOrderService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/order/purchase")
@Validated
@AllArgsConstructor
@Tag(name = "프로젝트 구매내역 관리 컨트롤러")
@Slf4j
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @Operation(
            summary = "구매 주문 생성",
            description = "결제 전에 주문 정보를 생성합니다."
    )
    public ResponseEntity<ApiResponse<String>> createPurchaseOrder(
            @Valid  @RequestBody PurchaseOrderRequestDto request
    ) {
        String response = purchaseOrderService.createOrder(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "구매 주문 생성 성공",response));
    }
}
