package com.funding.backend.domain.settlement.factory;

import java.time.LocalDateTime;
import java.time.YearMonth;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Getter
public class SettlementPeriod {

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final YearMonth yearMonth;

    public SettlementPeriod(LocalDateTime start, LocalDateTime end, YearMonth yearMonth) {
        this.start = start;
        this.end = end;
        this.yearMonth = yearMonth;
    }

}
