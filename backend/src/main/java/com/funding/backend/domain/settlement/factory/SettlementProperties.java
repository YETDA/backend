package com.funding.backend.domain.settlement.factory;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "settlement")
@Getter
@Setter
public class SettlementProperties {
    private int day;
    private int hour;
    private int minute;
}
