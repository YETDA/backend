package com.funding.backend.domain.settlement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SettlementMonthlyTotalAdminResponseDto {
    private int year;
    private int month;
    // 총 주문 금액
    private Long totalOrderAmount;

    //수수료 금액
    private Long feeAmount;

    //실제 정산 지급 금액
    //totalOrderAmount - feeAmount
    private Long payoutAmount;

    private Long projectCount;

}
