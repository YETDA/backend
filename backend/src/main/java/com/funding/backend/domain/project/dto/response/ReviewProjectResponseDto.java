package com.funding.backend.domain.project.dto.response;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewProjectResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 상태", example = "UNDER_REVIEW")
    private ProjectStatus status;

    @Schema(description = "프로젝트 제목", example = "개발자 노션 포트폴리오")
    private String title;

    @Schema(description = "프로젝트 소개", example = "이 프로젝트는 개발자들의 노션 포트폴리오를 소개합니다.")
    private String introduce;

    @Schema(description = "프로젝트 내용", example = "이 프로젝트는 개발자들이 노션을 활용하여 자신의 포트폴리오를 작성하는 방법을 소개합니다.")
    private String content;

    @Schema(description = "프로젝트 이미지 URL", example = "[ https://example.com/thumbnail.jpg, https://example.com/image2.jpg ]")
    private List<String> images;

    @Schema(description = "프로젝트 타입 (구매/기부)", example = "PURCHASE")
    private ProjectType type;

    @Schema(description = "프로젝트 종료일", example = "2023-12-31T23:59:59")
    private LocalDateTime endDate;

    @Schema(description = "제작자 ID", example = "145")
    private Long hostId;

    @Schema(description = "제작자 이름", example = "홍길동")
    private String hostName;

    @Schema(description = "제작자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String hostProfileImageUrl;

    public ReviewProjectResponseDto(Project project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.images = project.getProjectImage().stream()
                .map(ProjectImage::getImageUrl)
                .toList();
        this.type = project.getProjectType();
        this.hostName = project.getUser().getName();
        this.hostProfileImageUrl = project.getUser().getImage();

        if (project.getProjectType() == ProjectType.PURCHASE && project.getPurchase() != null) {
            this.endDate = null;
        } else if (project.getProjectType() == ProjectType.DONATION && project.getDonation() != null) {
            this.endDate = project.getDonation().getEndDate();
        } else {
            this.endDate = null;
        }
    }
}
