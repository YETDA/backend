package com.funding.backend.domain.donationReward.dto.response;

import com.funding.backend.domain.donationReward.entity.DonationReward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DonationRewardResponseDto {

    private String title;
    private String content;
    private Long price;
    private Long donationRewardId;

    public DonationRewardResponseDto(DonationReward donationReward) {
        this.donationRewardId = donationReward.getId();
        this.title = donationReward.getTitle();
        this.content = donationReward.getContent();
        this.price = donationReward.getPrice();
    }


}
