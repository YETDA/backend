package com.funding.backend.domain.purchase.dto.response;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectStatus;
import lombok.Getter;

@Getter
public class PurchaseListResponseDto {

    private Long projectId;
    private String title;
    private String introduce;
    private Long sellCount;
    private ProjectStatus projectStatus;
    private String hostName;
    private String projectImageUrl;

    public PurchaseListResponseDto(Project project, Long sellCount){
        this.title = project.getTitle();
        this.projectId = project.getId();
        this.introduce = project.getIntroduce();
        this.sellCount = sellCount;
        this.projectStatus = project.getProjectStatus();
        this.hostName = project.getUser().getName();
        this.projectImageUrl = project.getProjectImage().isEmpty() ? null :
            project.getProjectImage().getFirst().getImageUrl();
    }





}
