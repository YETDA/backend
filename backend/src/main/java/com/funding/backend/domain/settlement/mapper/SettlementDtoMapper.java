package com.funding.backend.domain.settlement.mapper;

import com.funding.backend.domain.project.entity.Project;
import java.time.LocalDateTime;

public interface SettlementDtoMapper<T> {
    T map(Project project, LocalDateTime from, LocalDateTime to, long totalAmount, long fee, long payout);
}
