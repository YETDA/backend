package com.funding.backend.domain.alarm.event.context;


import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewSuccessPurchaseContext {
    Long userId;
    String title;
    Long projectId;
    Long orderId;
    //해당 프로젝트에서 구매한 창작물 개수
    Long orderCount;
}
