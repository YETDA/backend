package com.funding.backend.domain.alarm.service.alert;

import com.funding.backend.domain.alarm.dto.response.AlarmDto;
import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.domain.alarm.event.context.NewPurchaseReceivedContext;
import com.funding.backend.domain.alarm.service.AlarmService;
import com.funding.backend.domain.alarm.strategy.AlarmStrategy;
import com.funding.backend.domain.alarm.strategy.factory.AlarmStrategyFactory;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.domain.user.service.UserService;
import com.funding.backend.global.toss.enums.TossPaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NewPurchaseReceivedService {

    private final AlarmStrategyFactory alarmStrategyFactory;
    private final AlarmService alarmService;
    private final UserService userService;

    @Transactional
    public void notifyPurchaseReceived(Long userId, Long projectUserId, String userName ,
                                       String title, Long totalPaymentAmount, TossPaymentStatus tossPaymentStatus, Long orderCount) {
        AlarmStrategy strategy = alarmStrategyFactory.getStrategy(AlarmType.PROJECT_PURCHASED);

        NewPurchaseReceivedContext context = new NewPurchaseReceivedContext(
                userId,
                userName,
                projectUserId,
                tossPaymentStatus,
                title,
                totalPaymentAmount,
                orderCount
        );

        User user  = userService.findUserById(projectUserId);

        if (strategy.shouldTrigger(context)) {

            String msg = strategy.generateMessage(context);
            alarmService.createNotification(new AlarmDto(
                    AlarmType.PROJECT_PURCHASED,
                    msg,
                    user.getId()
            ));
        }



    }


}
