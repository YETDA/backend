package com.funding.backend.domain.alarm.service.alert;


import com.funding.backend.domain.alarm.dto.response.AlarmDto;
import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.domain.alarm.event.context.NewSuccessPurchaseContext;
import com.funding.backend.domain.alarm.service.AlarmService;
import com.funding.backend.domain.alarm.strategy.AlarmStrategy;
import com.funding.backend.domain.alarm.strategy.factory.AlarmStrategyFactory;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewSuccessPurchaseService {

    private final AlarmStrategyFactory alarmStrategyFactory;
    private final AlarmService alarmService;
    private final UserService userService;

    @Transactional
    public void notifySuccessPurchase(Long userId, String title, ProjectStatus projectStatus
            ,TossPaymentStatus tossPaymentStatus,Long totalPaymentAmount, Long orderCount) {

        AlarmStrategy strategy = alarmStrategyFactory.getStrategy(AlarmType.PROJECT_PURCHASE_SUCCESS);
        NewSuccessPurchaseContext context = new NewSuccessPurchaseContext(
                userId,
                title,
                projectStatus,
                tossPaymentStatus,
                totalPaymentAmount,
                orderCount
        );

        User user  = userService.findUserById(userId);

        if (strategy.shouldTrigger(context)) {

            String msg = strategy.generateMessage(context);

            alarmService.createNotification(new AlarmDto(
                    AlarmType.PROJECT_PURCHASE_SUCCESS,
                    msg,
                    user.getId()
            ));
        }

    }
}
