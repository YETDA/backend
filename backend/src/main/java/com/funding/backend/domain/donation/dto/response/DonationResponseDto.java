package com.funding.backend.domain.donation.dto.response;

import lombok.Getter;

@Getter
public class DonationResponseDto {

    private Long projectId;

    public DonationResponseDto(Long projectId) {
        this.projectId = projectId;
    }
}
