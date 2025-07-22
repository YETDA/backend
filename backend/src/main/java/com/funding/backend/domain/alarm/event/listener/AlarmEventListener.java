package com.funding.backend.domain.alarm.event.listener;

import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.alarm.event.context.NewSuccessPurchaseContext;
import com.funding.backend.domain.alarm.service.alert.NewPurchaseProjectService;
import com.funding.backend.domain.alarm.service.alert.NewSuccessPurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmEventListener {
    private final NewPurchaseProjectService newPurchaseProjectService;
    private final NewSuccessPurchaseService newSuccessPurchaseService;

    // 비품 요청 알림
    @EventListener
    public void handleNewPurchaseProjectCreated(NewPurchaseProjectContext event) {
        newPurchaseProjectService.notifyCreatePurchaseProject(event.getUserId(),
                event.getTitle(), event.getProjectStatus(),event.getProjectType()
        ,event.getPricingPlan());
    }

    // 구매 완료 알림 ( 창작물을 구매한 사람에게 )
    @EventListener
    public void handleSuccessPurchase(NewSuccessPurchaseContext event) {
        newSuccessPurchaseService.notifySuccessPurchase(event.getUserId(),
                event.getTitle(), event.getProjectStatus(),event.getProjectType()
                ,event.getPricingPlan());
    }
}