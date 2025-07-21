package com.funding.backend.domain.settlement.dto.response;

import java.time.LocalDateTime;

public class OrderSummaryDto {
    private String orderId;
    private String customerEmail;
    private Long paidAmount;
    private LocalDateTime createdAt;
}
