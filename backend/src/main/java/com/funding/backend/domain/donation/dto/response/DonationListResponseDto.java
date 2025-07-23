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
    private String hostName;
    private String projectImageUrl;

    public DonationListResponseDto(Project project, Long sellCount){
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.projectStatus = project.getProjectStatus();
        this.sellCount = sellCount;
        this.hostName = project.getUser().getName();
        this.projectImageUrl = project.getProjectImage().isEmpty() ? null :
            project.getProjectImage().getFirst().getImageUrl();
    }

}
