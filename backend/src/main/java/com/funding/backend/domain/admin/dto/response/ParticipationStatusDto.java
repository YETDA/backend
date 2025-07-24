package com.funding.backend.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipationStatusDto {
    private long donatedProjectsCount;     // 사용자가 후원(기부)한 프로젝트 수
    private long donatedTotalAmount;       // 사용자가 후원(기부)한 총 금액
    private long purchasedProjectsCount;   // 사용자가 구매한 프로젝트 수
    private long purchasedTotalAmount;     // 사용자가 구매한 총 금액
}

