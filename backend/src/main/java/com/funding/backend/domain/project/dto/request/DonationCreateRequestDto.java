package com.funding.backend.domain.project.dto.request;

import com.funding.backend.domain.donation.dto.request.DonationProjectDetail;
import com.funding.backend.enums.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class DonationCreateRequestDto {

  @NotNull(message = "프로젝트 타입은 필수입니다.")
  private ProjectType projectType;

  @NotNull(message = "가격 정책은 필수입니다.")
  private Long pricingPlanId;

  @NotBlank(message = "제목은 필수입니다.")
  @Size(max = 100, message = "제목은 100자 이내여야 합니다.")
  private String title;

  @NotBlank(message = "소개글은 필수입니다.")
  @Size(max = 200, message = "소개글은 200자 이내여야 합니다.")
  private String introduce;

  @NotBlank(message = "내용은 필수입니다.")
  @Size(max = 1000, message = "소개글은 1000자 이내여야 합니다.")
  private String content;


  //이미지
  private List<MultipartFile> contentImage = new ArrayList<>();

  // 하위 타입 DTO
  private DonationProjectDetail donationProjectDetail;
}
