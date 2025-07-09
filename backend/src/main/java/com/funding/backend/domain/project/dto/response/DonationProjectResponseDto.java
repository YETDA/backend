package com.funding.backend.domain.project.dto.response;

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
    private List<String> contentImageUrls;


}
