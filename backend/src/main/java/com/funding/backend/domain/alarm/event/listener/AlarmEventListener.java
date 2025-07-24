package com.funding.backend.domain.alarm.event.listener;

import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.alarm.event.context.NewPurchaseReceivedContext;
import com.funding.backend.domain.alarm.event.context.NewSuccessPurchaseContext;
import com.funding.backend.domain.alarm.service.alert.NewPurchaseProjectService;
import com.funding.backend.domain.alarm.service.alert.NewPurchaseReceivedService;
import com.funding.backend.domain.alarm.service.alert.NewSuccessPurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlarmEventListener {
    private final NewPurchaseProjectService newPurchaseProjectService;
    private final NewSuccessPurchaseService newSuccessPurchaseService;
    private final NewPurchaseReceivedService newPurchaseReceivedService;

    // 새로운 프로젝트 생성 알림
    @EventListener
    public void handleNewPurchaseProjectCreated(NewPurchaseProjectContext event) {
        newPurchaseProjectService.notifyCreatePurchaseProject(event.getUserId(),
                event.getTitle(), event.getProjectStatus(),event.getProjectType()
        ,event.getPricingPlan());
    }

    // 구매 완료 알림 ( 창작물을 구매한 사람에게 )
    @EventListener
    public void handleSuccessPurchase(NewSuccessPurchaseContext event) {
        try {
            log.info("✅ [이벤트 시작] 구매자 알림 처리 시작: userId={}", event.getUserId());
            newSuccessPurchaseService.notifySuccessPurchase(event.getUserId(),
                    event.getTitle(), event.getProjectStatus(),event.getTossPaymentStatus()
                    ,event.getTotalPaymentAmount(),event.getOrderCount());
            // 알림 저장 또는 발송 로직
        } catch (Exception e) {
            log.error("❌ [에러 발생] 구매자 알림 처리 중 예외", e);
        }
    }

    // 구매 생성 알림 ( 프로젝트 주인에게 구매 알림 )
    @EventListener
    public void handlePurchaseReceived(NewPurchaseReceivedContext event) {
        newPurchaseReceivedService.notifyPurchaseReceived(event.getUserId(),
                event.getProjectUserId(), event.getUserName(), event.getTitle(),
                event.getTotalPaymentAmount()
                ,event.getTossPaymentStatus(),event.getOrderCount());
    }
}