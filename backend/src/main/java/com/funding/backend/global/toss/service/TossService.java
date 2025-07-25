package com.funding.backend.global.toss.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.alarm.event.context.NewPurchaseReceivedContext;
import com.funding.backend.domain.alarm.event.context.NewSuccessPurchaseContext;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.order.service.OrderService;
import com.funding.backend.domain.orderOption.entity.OrderOption;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.toss.dto.request.ConfirmPaymentRequestDto;
import com.funding.backend.global.toss.dto.response.TossPaymentsResponseDto;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

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
                .onStatus(status -> status.is4xxClientError(), clientResponse -> {
                    log.error("❌ Toss 결제 승인 요청 실패 - 4xx: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Toss 4xx Error: " + body));
                })
                .onStatus(status -> status.is5xxServerError(), clientResponse -> {
                    log.error("❌ Toss 서버 오류 - 5xx: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .map(body -> new RuntimeException("Toss 5xx Error: " + body));
                })

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
            order.setPurchaseSuccessTime(LocalDateTime.now());
            orderService.saveOrder(order); // 영속성 보장 확인
            alarmService(order.getOrderId());


        }else if (tossRes.getStatus() == TossPaymentStatus.ABORTED){
            log.info("삭제됨!!!!! ? ? ");
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

    public void alarmService(String orderId){
        Order order = orderService.findOrderByOrderIdWithOptions(orderId);
        //구매한 사용자에게 알림
        List<OrderOption> orderOptionList   = order.getOrderOptionList();
        log.info("알림 들어오나 ????????");
        Project project  = order.getProject();
        eventPublisher.publishEvent(new NewSuccessPurchaseContext(order.getUser().getId(), project.getTitle()
                ,project.getProjectStatus(),order.getOrderStatus(),order.getPaidAmount(),
                (long) orderOptionList.size()));


        log.info("2222222알림 들어오나 ????????222222");
        //해당 프로젝트 생성자에게 알림
        eventPublisher.publishEvent(new NewPurchaseReceivedContext(order.getUser().getId(),
                project.getUser().getName(),project.getUser().getId()
                ,order.getOrderStatus(), project.getTitle(),order.getPaidAmount(),
                (long) orderOptionList.size()));
    }


}
