package com.funding.backend.domain.donation.dto.response;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DonationListResponseDto {

    private Long projectId;
    private String title;
    private String introduce;
    private Long sellCount;
    private ProjectStatus projectStatus;

    public DonationListResponseDto(Project project, Long sellCount){
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.projectStatus = project.getProjectStatus();
        this.sellCount = sellCount;
    }

}
