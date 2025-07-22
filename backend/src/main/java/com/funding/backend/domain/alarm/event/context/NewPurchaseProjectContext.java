package com.funding.backend.domain.alarm.event.context;

import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewPurchaseProjectContext {
    public Long userId;
    public String title;
    public ProjectStatus projectStatus;
    public ProjectType projectType;
    public PricingPlan pricingPlan;

}
