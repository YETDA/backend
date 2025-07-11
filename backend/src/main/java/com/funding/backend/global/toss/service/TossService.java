package com.funding.backend.global.toss.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.funding.backend.global.toss.dto.request.ConfirmPaymentRequestDto;
import com.funding.backend.global.toss.dto.response.TossPaymentsResponseDto;
import java.io.IOException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
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



}
