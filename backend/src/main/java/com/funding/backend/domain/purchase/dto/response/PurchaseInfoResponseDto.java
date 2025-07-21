package com.funding.backend.domain.purchase.dto.response;

import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PurchaseInfoResponseDto {

    @Schema(description = "프로젝트 ID", example = "1")
    private Long id;

    @Schema(description = "프로젝트 제목", example = "개발자 노션 포트폴리오")
    private String title;

    @Schema(description = "프로젝트 소개", example = "이 프로젝트는 개발자들의 노션 포트폴리오를 소개합니다.")
    private String introduce;

    @Schema(description = "프로젝트 썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String projectFirstImage;

    @Schema(description = "프로젝트 타입 (구매/기부)", example = "PURCHASE")
    private ProjectType projectType;

    @Schema(description = "프로젝트 좋아요 수", example = "150")
    private int projectLikeCount;

    @Schema(description = "구매 프로젝트의 구매 수", example = "200")
    private Long sellingAmount;

    @Schema(description = "제작자 ID", example = "145")
    private Long hostId;

    @Schema(description = "제작자 이름", example = "홍길동")
    private String hostName;

    @Schema(description = "제작자 프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String hostProfileImageUrl;

    public PurchaseInfoResponseDto(Project project, Long sellingAmount) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.introduce = project.getIntroduce();
        this.projectFirstImage = project.getProjectImage().isEmpty() ? null : project.getProjectImage().getFirst().getImageUrl();
        this.projectType = project.getProjectType();
        this.projectLikeCount = project.getLikeList() != null ? project.getLikeList().size() : 0;
        this.sellingAmount = sellingAmount;
        this.hostId = project.getUser().getId();
        this.hostName = project.getUser().getName();
        this.hostProfileImageUrl = project.getUser().getImage();
    }
}