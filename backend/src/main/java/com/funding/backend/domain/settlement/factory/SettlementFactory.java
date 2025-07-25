package com.funding.backend.domain.settlement.factory;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.settlement.entity.Settlement;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementFactory {
    public Settlement create(Project project, List<Order> orders, SettlementPeriod period) {
        long total = orders.stream().mapToLong(Order::getPaidAmount).sum();
        double feeRate = project.getPricingPlan().getPlatformFee() / 100.0;
        long fee = Math.round(total * feeRate);
        long payout = total - fee;

        return Settlement.builder()
                .project(project)
                .totalOrderAmount(total)
                .feeAmount(fee)
                .payoutAmount(payout)
                .periodStart(period.getStart())
                .periodEnd(period.getEnd())
                .settledAt(LocalDateTime.now())
                .build();
    }
}
