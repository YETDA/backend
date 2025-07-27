package com.funding.backend.domain.settlement.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementProjectSummaryAdminResponseDto {

    private String creatorName;         // 창작자 이름
    private LocalDateTime projectCreatedAt;  // 프로젝트 생성일
    private LocalDateTime userCreatedAt;     // 사용자 가입일
    private String projectTitle;        // 프로젝트 이름
    private String projectDescription;  // 프로젝트 소개
    private Long totalOrderAmount;      // 총 주문 금액
    private Long feeAmount;             // 수수료 금액
    private Long payoutAmount;         // 실제 지급 금액
    private String accountNumber;  // 계좌 정보
}
