package com.funding.backend.domain.settlement.factory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementPeriodFactory {

    private final SettlementProperties props;

    public SettlementPeriod create(YearMonth currentMonth) {
        LocalDateTime start = currentMonth.minusMonths(1)
                .atDay(props.getDay())
                .atTime(props.getHour(), props.getMinute());

        LocalDateTime end = currentMonth
                .atDay(props.getDay())
                .atTime(props.getHour(), props.getMinute());

        return new SettlementPeriod(start, end, currentMonth);
    }
}
