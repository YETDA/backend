
package com.funding.backend.domain.settlement.mapper;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.settlement.dto.response.SettlementDetailListResponseDto;
import com.funding.backend.enums.SettlementStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class SettlementDetailListResponseMapper implements SettlementDtoMapper<SettlementDetailListResponseDto> {

    @Override
    public SettlementDetailListResponseDto map(Project project, LocalDateTime from, LocalDateTime to,
                                               long totalAmount, long fee, long payout) {
        return SettlementDetailListResponseDto.builder()
                .projectTitle(project.getTitle())
                .periodStart(from)
                .periodEnd(to)
                .projectId(project.getId())
                .totalOrderAmount(totalAmount)
                .feeAmount(fee)
                .payoutAmount(payout)
                .settlementStatus(SettlementStatus.WAITING)
                .projectStatus(project.getProjectStatus())
                .projectImageUrl(project.getProjectImage().isEmpty() ? null : project.getProjectImage().getFirst().getImageUrl())
                .build();
    }
}
