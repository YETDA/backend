package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class UnderReviewProjectResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 제목", example = "개발자 노션 포트폴리오")
    private String title;

    @Schema(description = "프로젝트 소개", example = "이 프로젝트는 개발자들의 노션 포트폴리오를 소개합니다.")
    private String introduce;

    @Schema(description = "프로젝트 내용", example = "이 프로젝트는 개발자들이 노션을 활용하여 자신의 포트폴리오를 작성하는 방법을 소개합니다.")
    private String content;

    @Schema(description = "프로젝트 썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnail;

    @Schema(description = "프로젝트 타입 (구매/기부)", example = "PURCHASE")
    private ProjectType projectType;

    @Schema(description = "프로젝트 종료일", example = "2023-12-31T23:59:59")
    private LocalDateTime projectEndDate;

    @Schema(description = "제작자 ID", example = "145")
    private Long hostId;

    @Schema(description = "제작자 이름", example = "홍길동")
    private String hostName;

    @Schema(description = "제작자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String hostProfileImageUrl;

    public UnderReviewProjectResponseDto(Project project) {
        long totalAmount = project.getOrderList().stream()
                .mapToLong(Order::getPaidAmount)
                .sum();

        this.id = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.thumbnail = project.getProjectImage().isEmpty() ? null : project.getProjectImage().getFirst().getImageUrl();
        this.projectType = project.getProjectType();
        this.hostName = project.getUser().getName();
        this.hostProfileImageUrl = project.getUser().getImage();

        if (project.getProjectType() == ProjectType.PURCHASE && project.getPurchase() != null) {
            this.projectEndDate = null; // 구매 프로젝트는 종료일이 없으므로 null로 설정
        } else if (project.getProjectType() == ProjectType.DONATION && project.getDonation() != null) {
            this.projectEndDate = project.getDonation().getEndDate();
        } else {
            this.projectEndDate = null;
        }
    }
}
