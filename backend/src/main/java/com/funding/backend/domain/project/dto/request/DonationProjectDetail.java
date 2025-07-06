package com.funding.backend.domain.project.dto.request;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DonationProjectDetail {
    private Long goalAmount;
    private LocalDate startDate;
    private LocalDate endDate;
}
