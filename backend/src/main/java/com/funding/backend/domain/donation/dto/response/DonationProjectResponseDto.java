package com.funding.backend.domain.donation.dto.response;

import com.funding.backend.domain.project.dto.response.ProjectResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DonationProjectResponseDto implements ProjectResponseDto {
    private Long projectId;
    private String title;
    private String introduce;
    private String content;
    private String bank;
    private String account;
    private List<String> contentImageUrls;
}
