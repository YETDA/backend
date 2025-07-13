package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.orderOption.dto.response.OrderOptionResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchaseOption.dto.response.PurchaseOptionResponseDto;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AuditProjectResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 상태", example = "UNDER_AUDIT")
    private ProjectStatus status;

    @Schema(description = "프로젝트 타입 (구매/기부)", example = "PURCHASE")
    private ProjectType type;

    @Schema(description = "프로젝트 카테고리", example = "템플릿")
    private String category;

    @Schema(description = "프로젝트 제목", example = "개발자 노션 포트폴리오")
    private String title;

    @Schema(description = "프로젝트 소개", example = "이 프로젝트는 개발자들의 노션 포트폴리오를 소개합니다.")
    private String introduce;

    @Schema(description = "프로젝트 내용", example = "이 프로젝트는 개발자들이 노션을 활용하여 자신의 포트폴리오를 작성하는 방법을 소개합니다.")
    private String content;

    @Schema(description = "프로젝트 이미지 URL", example = "[ https://example.com/thumbnail.jpg, https://example.com/image2.jpg ]")
    private List<String> images;

    @Schema(description = "프로젝트 목표 금액", example = "5000000")
    private Long priceGoal;

    @Schema(description = "프로젝트 등록일", example = "2023-01-01T00:00:00")
    private LocalDateTime createdDate;

    @Schema(description = "프로젝트 시작일", example = "2023-01-01T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "프로젝트 종료일", example = "2023-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "Git 주소", example = "https://www.github.com/EXAMPLE")
    private String gitAddress;

    @Schema(description = "배포 주소", example = "https://www.example.com")
    private String deployAddress;

    @Schema(description = "평균 배송 시간", example = "3일")
    private String averageDeliveryTime;

    @Schema(description = "제작자 ID", example = "145")
    private Long hostId;

    @Schema(description = "제작자 이름", example = "홍길동")
    private String hostName;

    @Schema(description = "제작자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String hostProfileImageUrl;

    @Schema(description = "구매 옵션 목록", example = "[ { \"title\": \"이력서 템플릿\", \"content\": \"노션 이력서 템플릿입니다.\", \"price\": 10000, \"fileUrl\": \"https://example.com/file1.zip\", \"optionStatus\": \"AVAILABLE\", \"providingMethod\": \"DOWNLOAD\", \"purchaseOptionId\": 1 } ]")
    private List<PurchaseOptionResponseDto> purchaseOptions;

    public AuditProjectResponseDto(Project project) {
        this.id = project.getId();
        this.status = project.getProjectStatus();
        this.type = project.getProjectType();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.content = project.getContent();
        this.images = project.getProjectImage().stream()
                .map(ProjectImage::getImageUrl)
                .toList();
        this.createdDate = project.getCreatedAt();
        this.hostId = project.getUser().getId();
        this.hostName = project.getUser().getName();
        this.hostProfileImageUrl = project.getUser().getImage();

        if (project.getProjectType() == ProjectType.PURCHASE && project.getPurchase() != null) {
            this.startDate = null;
            this.endDate = null;
            this.category = project.getPurchase().getPurchaseCategory().getName();
            this.priceGoal = null;
            this.purchaseOptions = project.getPurchase().getPurchaseOptionList().stream()
                    .map(PurchaseOptionResponseDto::new)
                    .toList();
            this.gitAddress = project.getPurchase().getGitAddress();
            this.deployAddress = null;
            this.averageDeliveryTime = project.getPurchase().getAverageDeliveryTime();
        } else if (project.getProjectType() == ProjectType.DONATION && project.getDonation() != null) {
            this.startDate = project.getDonation().getStartDate();
            this.endDate = project.getDonation().getEndDate();
            this.category = project.getDonation().getMainCategory().getName();
            this.priceGoal = project.getDonation().getPriceGoal();
            this.purchaseOptions = null;
            this.gitAddress = project.getDonation().getGitAddress();
            this.deployAddress = project.getDonation().getDeployAddress();
            this.averageDeliveryTime = null;
        } else {
            this.startDate = null;
            this.endDate = null;
            this.category = null;
            this.priceGoal = null;
            this.purchaseOptions = null;
            this.gitAddress = null;
            this.deployAddress = null;
            this.averageDeliveryTime = null;
        }
    }
}
