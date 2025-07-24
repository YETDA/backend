package com.funding.backend.domain.settlement.mapper;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailResponseDto;
import com.funding.backend.enums.SettlementStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SettlementDetailResponseMapper implements SettlementDtoMapper<SettlementDetailResponseDto> {

    @Override
    public SettlementDetailResponseDto map(Project project, LocalDateTime from, LocalDateTime to,
                                           long totalAmount, long fee, long payout) {
        return SettlementDetailResponseDto.builder()
                .projectTitle(project.getTitle())
                .periodStart(from)
                .periodEnd(to)
                .projectId(project.getId())
                .totalOrderAmount(totalAmount)
                .feeAmount(fee)
                .payoutAmount(payout)
                .settlementStatus(SettlementStatus.WAITING)
                .build();
    }
}
