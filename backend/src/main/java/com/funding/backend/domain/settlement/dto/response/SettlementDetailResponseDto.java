package com.funding.backend.domain.settlement.dto.response;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.SettlementStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SettlementDetailResponseDto {
    private String projectTitle;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long totalOrderAmount;
    private Long feeAmount;
    private Long payoutAmount;
    private SettlementStatus settlementStatus;

}
