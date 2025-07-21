package com.funding.backend.domain.donationMilestone.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DonationMilestoneCreateDto {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private LocalDate dueDate;

}
