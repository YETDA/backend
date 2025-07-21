package com.funding.backend.domain.donationMilestone.dto.response;

import com.funding.backend.domain.donationMilestone.entity.DonationMilestone;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DonationMilestoneResponseDto {

    private String title;
    private String content;
    private LocalDate dueDate;
    private Long donationMilestoneId;

    public DonationMilestoneResponseDto(DonationMilestone donationMilestone) {
        this.title = donationMilestone.getTitle();
        this.content = donationMilestone.getContent();
        this.dueDate = donationMilestone.getDueDate();
        this.donationMilestoneId = donationMilestone.getId();
    }

}
