package com.funding.backend.global.toss.controller;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.dto.request.ConfirmPaymentRequestDto;
import com.funding.backend.global.toss.dto.response.*;
import com.funding.backend.global.toss.enums.OrderStatus;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import com.funding.backend.global.toss.service.TossService;
import com.funding.backend.global.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RestController;

@Tag(name = "토스 결제 API", description = "Toss 결제 승인 요청 및 상태 처리를 담당합니다.")
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
    @Operation(summary = "토스 결제 승인 요청", description = "...")
    public ResponseEntity<ApiResponse<Void>> confirmPayment(@RequestBody ConfirmPaymentRequestDto dto) {
        try {
            tossService.confirmAndProcessPayment(dto);

            return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "결제 성공", null));
        } catch (BusinessLogicException e) {
            // 결제 금액 불일치 등의 경우만 삭제 수행
            if (e.getExceptionCode() == ExceptionCode.MISMATCHED_PAYMENT_AMOUNT) {
                try {
                    tossService.deletePayment(dto);
                } catch (Exception deleteEx) {
                    log.warn("주문 삭제 중 오류 (이미 삭제되었을 수 있음): {}", deleteEx.getMessage());
                }
            }
            return ResponseEntity
                    .status(e.getExceptionCode().getStatus())
                    .body(ApiResponse.of(e.getExceptionCode().getStatus(), e.getMessage(), null));
        } catch (Exception e) {
            try {
                tossService.deletePayment(dto);
            } catch (Exception deleteEx) {
                log.warn("주문 삭제 중 오류 (이미 삭제되었을 수 있음): {}", deleteEx.getMessage());
            }
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류", null));
        }
    }

//    @PostMapping("/success/alarm/{orderId}")
//    public ResponseEntity<ApiResponse<Void>> notifyPurchaseSuccess(@PathVariable String orderId) {
//        tossService.alarmService(orderId);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(ApiResponse.of(HttpStatus.OK.value(), "알림 생성 완료", null));
//    }




//
//    /**
//     * 인증실패처리
//     * @param request
//     * @param model
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/fail", method = RequestMethod.GET)
//    public String failPayment(HttpServletRequest request, Model model) throws Exception {
//        String failCode = request.getParameter("code");
//        String failMessage = request.getParameter("message");
//
//        model.addAttribute("code", failCode);
//        model.addAttribute("message", failMessage);
//
//        return "/fail";
//    }
}
