package com.funding.backend.domain.alarm.service.alert;

import com.funding.backend.domain.alarm.dto.response.AlarmDto;
import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.domain.alarm.event.context.NewPurchaseProjectContext;
import com.funding.backend.domain.alarm.service.AlarmService;
import com.funding.backend.domain.alarm.strategy.AlarmStrategy;
import com.funding.backend.domain.alarm.strategy.factory.AlarmStrategyFactory;
import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewPurchaseProjectService {
    private final AlarmStrategyFactory alarmStrategyFactory;
    private final AlarmService alarmService;
    private final UserService userService;

    @Transactional
    public void notifyCreatePurchaseProject(Long userId, String title, ProjectStatus status, ProjectType type, PricingPlan pricingPlan) {
        AlarmStrategy strategy = alarmStrategyFactory.getStrategy(AlarmType.PURCHASE_PROJECT_REQUEST);

        NewPurchaseProjectContext context = new NewPurchaseProjectContext(
                userId,
                title,
                status,
                type,
                pricingPlan
        );

        List<User> adminUserList = userService.findAllByAdmin();
        for (User admin : adminUserList) {
        if (strategy.shouldTrigger(context)) {
            String msg = strategy.generateMessage(context);

            alarmService.createNotification(new AlarmDto(
                    AlarmType.PURCHASE_PROJECT_REQUEST,
                    msg,
                    admin.getId()
            ));
        }
    }

    }

}

