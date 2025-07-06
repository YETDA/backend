package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.enums.ProvidingMethod;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class PurchaseProjectResponseDto {

    private Long projectId;

    // Project 기본 정보
    private String title;
    private String introduce;
    private String coverImage;
    private List<String> projectImages;

    private String projectStatus;
    private String projectType;

    // 유저 정보
    private Long userId;
    private String userNickname;

    // PricingPlan 정보
    private String pricingPlanName;

    // Purchase 관련 정보
    private PurchaseCategory purchaseCategory;
    private String gitAddress;
    private ProvidingMethod providingMethod;
    private String averageDeliveryTime;
    private String fileUrl;

    public PurchaseProjectResponseDto(Project project, Purchase purchase) {
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.coverImage = project.getCoverImage();
        this.projectImages = project.getProjectImage().stream()
                .map(ProjectImage::getImageUrl) // ProjectImage 엔티티에 getUrl() 필드가 있다고 가정
                .toList();

        this.projectStatus = project.getProjectStatus().getLabel();
        this.projectType = project.getProjectType().getLabel();

        this.userId = project.getUser().getId();
        this.userNickname = project.getUser().getName(); // getUsername()일 수도 있음

        this.pricingPlanName = project.getPricingPlan().getName();

        this.purchaseCategory = project.getPurchaseCategory();
        this.gitAddress = purchase.getGitAddress();
        this.providingMethod = purchase.getProvidingMethod();
        this.averageDeliveryTime = purchase.getAverageDeliveryTime();
        this.fileUrl = purchase.getFile();
    }

}