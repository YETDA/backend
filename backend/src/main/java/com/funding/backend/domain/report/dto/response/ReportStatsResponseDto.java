package com.funding.backend.domain.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportStatsResponseDto {
    private long totalCount;
    private long pendingCount;
    private long approvedCount;
    private long rejectedCount;
}
