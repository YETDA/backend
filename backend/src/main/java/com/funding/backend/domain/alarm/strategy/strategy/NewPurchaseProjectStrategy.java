package com.funding.backend.domain.alarm.strategy.strategy;

import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.alarm.strategy.AlarmStrategy;
import com.funding.backend.enums.ProjectStatus;

public class NewPurchaseProjectStrategy implements AlarmStrategy {

    @Override
    public String generateMessage(Object context) {
        NewPurchaseProjectContext project = (NewPurchaseProjectContext) context;
        return project.getTitle() + "새 프로젝트가 등록되었습니다. 관리자의 승인을 기다리고 있습니다.";
    }

    @Override
    public boolean shouldTrigger(Object context) {
        NewPurchaseProjectContext project = (NewPurchaseProjectContext) context;
        return project.getProjectStatus().equals(ProjectStatus.UNDER_AUDIT);
    }

}
