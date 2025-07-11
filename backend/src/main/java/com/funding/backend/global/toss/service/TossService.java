package com.funding.backend.global.toss.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.dto.request.ConfirmPaymentRequestDto;
import com.funding.backend.global.toss.dto.response.TossPaymentsResponseDto;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossService {

    @Value("${toss.secret-key}")
    private  String tossSecretKey;


    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    private final WebClient webClient = WebClient.builder().
            baseUrl("https://api.tosspayments.com/v1/payments")
            .build();

    public ResponseEntity<TossPaymentsResponseDto> requestPaymentConfirm(ConfirmPaymentRequestDto confirmPaymentRequest) throws IOException, InterruptedException {
        String tossOrderId = confirmPaymentRequest.getOrderId();
        long amount = confirmPaymentRequest.getAmount();
        String tossPaymentKey = confirmPaymentRequest.getPaymentKey();
        log.info("amount: {}",amount);
        // 승인 요청에 사용할 JSON 객체를 만듭니다.
        JsonNode requestObj = objectMapper.createObjectNode()
                .put("paymentKey", tossPaymentKey)
                .put("orderId", tossOrderId)
                .put("amount", amount);

        return webClient.post()
                .uri("/confirm")
                .header("Authorization", getAuthorizations())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestObj)
                .retrieve()
                .toEntity(TossPaymentsResponseDto.class)
                .block();
    }

    private String getAuthorizations(){
        String rawAuthKey = tossSecretKey + ":";
        String encodedKey = Base64.getEncoder().encodeToString(rawAuthKey.getBytes());
        log.info("encodeKey {}", encodedKey);
        return  "Basic " + encodedKey;
    }

    public void confirmAndProcessPayment(ConfirmPaymentRequestDto dto) throws IOException, InterruptedException {
        TossPaymentsResponseDto tossRes = validateAndGetTossResponse(dto);
        Order order = orderService.findOrderByOrderId(tossRes.getOrderId());
        if (!order.getPaidAmount().equals(dto.getAmount())) {
            throw new BusinessLogicException(ExceptionCode.MISMATCHED_PAYMENT_AMOUNT);
        }

        if (tossRes.getStatus() == TossPaymentStatus.DONE) {
            order.setPayType(tossRes.getMethod());
            order.setOrderStatus(tossRes.getStatus());
            order.setPaymentKey(dto.getPaymentKey());
            orderService.saveOrder(order); // 영속성 보장 확인
        }else if (tossRes.getStatus() == TossPaymentStatus.ABORTED){
            orderService.deleteOrder(order);
        }
    }

    public void deletePayment(ConfirmPaymentRequestDto dto) throws IOException, InterruptedException {
        TossPaymentsResponseDto tossRes = validateAndGetTossResponse(dto);
        Order order = orderService.findOrderByOrderId(tossRes.getOrderId());
        orderService.deleteOrder(order);
    }

    private TossPaymentsResponseDto validateAndGetTossResponse(ConfirmPaymentRequestDto dto)
            throws IOException, InterruptedException {
        ResponseEntity<TossPaymentsResponseDto> response = requestPaymentConfirm(dto);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new BusinessLogicException(ExceptionCode.PAYMENT_CONFIRM_FAILED);
        }
        return response.getBody();
    }






}
