package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import java.time.LocalDateTime;
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

    private Long mainCategoryId;
    private String mainCategoryName;
    private List<SubjectCategory> projectSubCategories;
    private Long priceGoal;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String gitAddress;
    private String deployAddress;

    public DonationProjectResponseDto(Project project, Donation donation, List<SubjectCategory> projectSubCategories){

        this.projectId = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.content = project.getContent();
        this.contentImageUrls = project.getProjectImage().stream()
            .map(ProjectImage::getImageUrl)
            .toList(); // 또는 collect(Collectors.toList()) in Java 8

        if (donation.getMainCategory() != null) {
            this.mainCategoryId = donation.getMainCategory().getId();
            this.mainCategoryName = donation.getMainCategory().getName();
        }
        this.projectSubCategories = projectSubCategories;

        this.priceGoal = donation.getPriceGoal();
        this.startDate = donation.getStartDate();
        this.endDate = donation.getEndDate();
        this.gitAddress = donation.getGitAddress();
        this.deployAddress = donation.getDeployAddress();

    }
}
