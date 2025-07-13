package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseProjectResponseDto implements ProjectResponseDto {
    private Long projectId;
    private String title;
    private String introduce;
    private String content;
    private String gitAddress;
    private Long purchaseCategoryId;
    private String purchaseCategoryName;
    private String averageDeliveryTime;
    private List<String> contentImageUrls;
    private Long userId;
    private String name;
    private String userProfileImage;
    private Long projectCount;
    private Long followerCount;
    private String userIntroduce;
    private String email;
    private List<PurchaseOptionResponseDto> purchaseOptions;


    public PurchaseProjectResponseDto(Project project, Purchase purchase, List<PurchaseOptionResponseDto> purchaseOptions
    ,Long projectCount, Long followerCount) {
        this.projectId = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.content = project.getContent();
        this.gitAddress = purchase.getGitAddress();

        if (purchase.getPurchaseCategory() != null) {
            this.purchaseCategoryId = purchase.getPurchaseCategory().getId();
            this.purchaseCategoryName = purchase.getPurchaseCategory().getName();
        }

        this.averageDeliveryTime = purchase.getAverageDeliveryTime();
        this.contentImageUrls = project.getProjectImage().stream()
                .map(ProjectImage::getImageUrl)
                .toList(); // 또는 collect(Collectors.toList()) in Java 8
        this.purchaseOptions = purchaseOptions;
        this.userId = project.getUser().getId();
        this.name=project.getUser().getName();
        this.userProfileImage= project.getUser().getImage();
        this.userIntroduce = project.getUser().getIntroduce();
        this.email = project.getUser().getEmail();
        this.projectCount = projectCount;
        this.followerCount= followerCount;

    }


}
