package com.funding.backend.domain.donation.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
// 프로젝트 생성할때 쓰이는 rewardRequestDto
public class DonationRewardRequestDto {

    private String title;
    private String content;
    private Long price;

}
