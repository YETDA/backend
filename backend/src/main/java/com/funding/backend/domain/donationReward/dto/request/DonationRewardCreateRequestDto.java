package com.funding.backend.domain.donationReward.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DonationRewardCreateRequestDto {

    @NotBlank
    private final String title;

    @NotBlank
    private final String content;

    @NotNull
    private final Long price;

    public static DonationRewardCreateRequestDto from(String title, String content, Long price) {
        return DonationRewardCreateRequestDto.builder()
            .title(title)
            .content(content)
            .price(price)
            .build();
    }
}
