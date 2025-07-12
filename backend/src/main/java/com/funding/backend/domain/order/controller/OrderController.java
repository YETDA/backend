package com.funding.backend.domain.order.controller;

import com.funding.backend.domain.order.dto.response.OrderResponseDto;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/order")
@Validated
@AllArgsConstructor
@Tag(name = "구매 내역 관리 컨트롤러")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @Operation(
            summary = "사용자 주문(구매 내역) 목록 조회",
            description = "사용자가 구매한 주문 목록을 페이지네이션 방식으로 조회합니다.( 기부형, 창작물형 포함) "
    )
    public ResponseEntity<ApiResponse<Page<OrderResponseDto>>> getUserOrderList(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<OrderResponseDto> response = orderService.getUserOrderListResponse(page, size);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "유저 구매 리스트 조회 완료",response));
    }


}
