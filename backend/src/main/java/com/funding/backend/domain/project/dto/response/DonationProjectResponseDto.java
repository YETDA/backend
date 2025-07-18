package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donationReward.dto.response.DonationRewardResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.projectSubCategory.dto.request.ProjectSubRequestDto;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
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
    private List<ProjectSubRequestDto> projectSubCategories;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String gitAddress;
    private String deployAddress;
    private List<DonationRewardResponseDto> donationRewards;

    private Long userId;
    private String name;
    private String userProfileImage;
    private Long projectCount;
    private Long followerCount;
    private String userIntroduce;
    private String email;

    public DonationProjectResponseDto(Project project, Donation donation, List<DonationRewardResponseDto> donationRewards,
        Long projectCount, Long followerCount){

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
        this.projectSubCategories = donation.getProjectSubCategories().stream()
            .map(psc -> new ProjectSubRequestDto(
                psc.getSubjectCategory().getId(),
                psc.getSubjectCategory().getName()
            ))
            .collect(Collectors.toList());
        this.startDate = donation.getStartDate();
        this.endDate = donation.getEndDate();
        this.gitAddress = donation.getGitAddress();
        this.deployAddress = donation.getDeployAddress();
        this.donationRewards = donationRewards;

        this.userId = project.getUser().getId();
        this.name=project.getUser().getName();
        this.userProfileImage= project.getUser().getImage();
        this.userIntroduce = project.getUser().getIntroduce();
        this.email = project.getUser().getEmail();
        this.projectCount = projectCount;
        this.followerCount= followerCount;

    }
}
