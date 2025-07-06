package com.funding.backend.domain.project.dto.response;

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

    private ProjectStatus projectStatus;
    private ProjectType projectType;

    // 유저 정보
    private Long userId;
    private String userNickname;

    // PricingPlan 정보
    private Long pricingPlanId;
    private String pricingPlanName;
    private int pricingPlanPrice;

    // Purchase 관련 정보
    private String purchaseCategory;
    private String gitAddress;
    private ProvidingMethod providingMethod;
    private String averageDeliveryTime;
    private String fileUrl;
}