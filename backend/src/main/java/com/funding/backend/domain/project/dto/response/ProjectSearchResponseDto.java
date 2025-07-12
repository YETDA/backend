package com.funding.backend.domain.project.dto.response;

import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectSearchResponseDto implements ProjectResponseDto {
    private long id;
    private String title;
    private String introduce;
    private ProjectType projectType;
    private ProjectStatus projectStatus;
    private String thumbnailUrl;
    private String ownerName;
    private String categoryName;

    /*
    별도 계산 필요

    private Long fundedAmount; //판매금액 또는 후원액 합산
    private Integer achievementRate;   // 달성률 (추후 구현)
    private Integer likeCount;         // 좋아요 수 (추후 구현)
    private Integer supporterCount;    // 구매자/후원자 수 (추후 구현)
    private Integer daysLeft;          // 남은 일수 (추후 구현)

     */


}
