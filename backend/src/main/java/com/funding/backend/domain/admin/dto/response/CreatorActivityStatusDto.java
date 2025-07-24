package com.funding.backend.domain.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreatorActivityStatusDto {
    // 기부형 프로젝트 개설 수
    private long donationProjectsCreated;
    // 기부형 프로젝트 정산 요청 횟수
    private long donationSettlementRequests;
    // 기부형 프로젝트 정산 받은 총 금액
    private long donationTotalPayout;

    // 구매형 프로젝트 개설 수
    private long purchaseProjectsCreated;
    // 구매형 프로젝트 정산 요청 횟수
    private long purchaseSettlementRequests;
    // 구매형 프로젝트 정산 받은 총 금액
    private long purchaseTotalPayout;
}
