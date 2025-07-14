package com.funding.backend.domain.project.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ProjectCountResponseDto {

    private Long allProjectCount;
    private Long donationProjectCount;
    private Long purchaseProjectCount;

    public ProjectCountResponseDto(Long all, Long donation, Long purchase){
        this.allProjectCount=all;
        this.donationProjectCount=donation;
        this.purchaseProjectCount = purchase;
    }
}
