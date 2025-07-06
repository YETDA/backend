package com.funding.backend.domain.project.dto.request;

import com.funding.backend.enums.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class ProjectCreateRequestDto {

    @NotNull(message = "프로젝트 타입은 필수입니다.")
    private ProjectType projectType;

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotNull(message = "가격 정책은 필수입니다.")
    private Long pricingPlanId;

    @NotBlank(message = "커버 이미지는 필수입니다.")
    private String coverImage;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
    private String title;

    //소개 필요한가 ..?
    @NotBlank(message = "소개글은 필수입니다.")
    @Size(max = 200, message = "소개글은 200자 이내여야 합니다.")
    private String introduce;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(max = 1000, message = "소개글은 1000자 이내여야 합니다.")
    private String content;

    private String field; // 선택 값이므로 필수 아님

    // 하위 타입 DTO
    private DonationProjectDetail donationDetail;
    private PurchaseProjectDetail purchaseDetail;
}
