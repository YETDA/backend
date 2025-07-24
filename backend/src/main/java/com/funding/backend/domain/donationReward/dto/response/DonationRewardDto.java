package com.funding.backend.domain.donationReward.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DonationRewardDto {

    private Long id;
    private String title;
    private Long price;
}
