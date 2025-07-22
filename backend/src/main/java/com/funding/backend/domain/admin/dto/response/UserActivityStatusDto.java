package com.funding.backend.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserActivityStatusDto {
    private long createdProjectsCount;        // 개설한 프로젝트 수
    private long donatedProjectsCount;      // 후원(기부)한 프로젝트 수
    private long donatedTotalAmount;        // 후원(기부) 총 금액
    private long purchasedProjectsCount;      // 구매한 프로젝트 수
    private long purchasedTotalAmount;        // 구매한 총 금액
    private long settlementRequestCount;      // 정산 요청 횟수
}