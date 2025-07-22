package com.funding.backend.domain.alarm.strategy.strategy;

import com.funding.backend.domain.alarm.event.context.NewPurchaseReceivedContext;
import com.funding.backend.domain.alarm.event.context.NewSuccessPurchaseContext;
import com.funding.backend.domain.alarm.strategy.AlarmStrategy;
import com.funding.backend.global.toss.enums.TossPaymentStatus;

public class NewPurchaseReceivedStrategy implements AlarmStrategy  {
    @Override
    public String generateMessage(Object context) {
        NewPurchaseReceivedContext project = (NewPurchaseReceivedContext) context;
        return project.getTitle() + " 프로젝트의 창작물을 " + project.getUserName() + "님이 구매하였습니다.\n"
                + "구매한 창작물 개수는 " + project.getOrderCount() + "개이며,\n"
                + "총 결제 금액은 " + project.getTotalPaymentAmount() + "원입니다.";
    }

    @Override
    public boolean shouldTrigger(Object context) {
        //결제가 완료된 주문에서만 trigger 동작
        NewPurchaseReceivedContext project = (NewPurchaseReceivedContext) context;
        return true;
    }
}
