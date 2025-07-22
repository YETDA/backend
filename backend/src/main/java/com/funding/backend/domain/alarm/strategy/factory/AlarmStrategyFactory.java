package com.funding.backend.domain.alarm.strategy.factory;


import com.funding.backend.domain.alarm.enums.AlarmType;
import com.funding.backend.domain.alarm.strategy.AlarmStrategy;
import com.funding.backend.domain.alarm.strategy.strategy.NewPurchaseProjectStrategy;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import jakarta.annotation.PostConstruct;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class AlarmStrategyFactory {

    private final Map<AlarmType, AlarmStrategy> strategyMap = new EnumMap<>(AlarmType.class);

    @PostConstruct
    public void init() {
        // 매니저
        strategyMap.put(AlarmType.PURCHASE_PROJECT_REQUEST, new NewPurchaseProjectStrategy());

        //회원
        strategyMap.put(AlarmType.PROJECT_PURCHASED, new NewPurchaseProjectStrategy());
    }

    public AlarmStrategy getStrategy(AlarmType type) {
        AlarmStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new BusinessLogicException(ExceptionCode.ALARM_STRATEGY_NOT_FOUND);
        }
        return strategy;
    }
}