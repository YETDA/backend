package com.funding.backend.global.toss.controller;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.global.toss.dto.request.ConfirmPaymentRequestDto;
import com.funding.backend.global.toss.dto.response.*;
import com.funding.backend.global.toss.enums.OrderStatus;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.global.toss.service.TossService;
import com.funding.backend.global.utils.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/toss")
@RequiredArgsConstructor
@Slf4j
public class TossController {

    @Value("${toss.secret-key}")
    private  String tossSecretKey;

    private final TossService tossService;
    private final OrderService orderService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@RequestBody ConfirmPaymentRequestDto confirmPaymentRequestDto) {
        try {
            ResponseEntity<TossPaymentsResponseDto> response = tossService.requestPaymentConfirm(confirmPaymentRequestDto);
            Order requestOrder = orderService.findOrderByOrderId(confirmPaymentRequestDto.getOrderId());
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                TossPaymentsResponseDto tossPaymentsResponse = response.getBody();
                log.info("tossPayments body: {}", tossPaymentsResponse);

                Order order = orderService.findOrderByOrderId(tossPaymentsResponse.getOrderId());
                order.setPayType(tossPaymentsResponse.getMethod());
                order.setOrderStatus(requestOrder.getOrderStatus());
                orderService.saveOrder(order); // 영속 상태면 생략 가능

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(ApiResponse.of(HttpStatus.OK.value(), "결제 성공", null));
            }
            requestOrder.setOrderStatus(requestOrder.getOrderStatus());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.of(HttpStatus.BAD_REQUEST.value(), "결제 승인 실패", null));

        } catch (Exception e) {
            log.error("결제 승인 중 예외 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류", null));
        }
    }


    /**
     * 인증실패처리
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fail", method = RequestMethod.GET)
    public String failPayment(HttpServletRequest request, Model model) throws Exception {
        String failCode = request.getParameter("code");
        String failMessage = request.getParameter("message");

        model.addAttribute("code", failCode);
        model.addAttribute("message", failMessage);

        return "/fail";
    }
}
