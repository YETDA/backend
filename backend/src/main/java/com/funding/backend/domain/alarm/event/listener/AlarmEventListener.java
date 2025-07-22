package com.funding.backend.domain.alarm.event.listener;

import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.alarm.service.alert.NewPurchaseProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlarmEventListener {
    private final NewPurchaseProjectService newPurchaseProjectService;

    // 비품 요청 알림
    @EventListener
    public void handleNewPurchaseProjectCreated(NewPurchaseProjectContext event) {
        newPurchaseProjectService.notifyCreatePurchaseProject(event.getUserId(),
                event.getTitle(), event.getProjectStatus(),event.getProjectType()
        ,event.getPricingPlan());
    }


}