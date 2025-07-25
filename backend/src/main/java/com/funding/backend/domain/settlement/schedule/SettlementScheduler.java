package com.funding.backend.domain.settlement.schedule;


import com.funding.backend.domain.settlement.service.SettlementService;
import com.funding.backend.enums.ProjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SettlementScheduler {


    private final SettlementService settlementService;

    @Scheduled(cron = "0 0 15 20 * ?") //// 매월 20일 03:00 실행
    public void settleMonthlyProjects() {
        log.info("[정산 스케줄러] 월별 정산 시작");
        settlementService.executeMonthlySettlement(ProjectType.PURCHASE);
        settlementService.executeMonthlySettlement(ProjectType.DONATION);
        log.info("[정산 스케줄러] 월별 정산 완료");
    }
}


