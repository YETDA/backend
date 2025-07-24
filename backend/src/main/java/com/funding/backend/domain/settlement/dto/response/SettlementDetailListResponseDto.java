package com.funding.backend.domain.settlement.dto.response;

import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.SettlementStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SettlementDetailListResponseDto {
    private String projectTitle;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long totalOrderAmount;
    private Long feeAmount;
    private Long payoutAmount;
    private SettlementStatus settlementStatus;
    private ProjectStatus projectStatus;
    private String projectImageUrl;
    private Long projectId;

}
