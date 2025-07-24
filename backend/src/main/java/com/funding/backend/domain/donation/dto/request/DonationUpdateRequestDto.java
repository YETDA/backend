package com.funding.backend.domain.donation.dto.request;

import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.enums.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
public class DonationUpdateRequestDto {

  @NotNull(message = "프로젝트 타입은 필수입니다.")
  private ProjectType projectType;

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

  @NotNull(message = "후원 카테고리는 필수입니다.")
  private Long mainCategoryId;

  @Size(max = 2, message = "상세 카테고리는 최대 2개까지 선택 가능합니다.")
  private List<Long> subCategoryIds;

  @NotNull(message = "목표 금액은 필수입니다.")
  private Long priceGoal;

  @NotNull(message = "시작일은 필수입니다.")
  private LocalDate startDate;

  @NotNull(message = "종료일은 필수입니다.")
  private LocalDate endDate;

  @NotBlank(message = "배포 주소는 필수입니다.")
  private String deployAddress;

  private String gitAddress;

  private String appStoreAddress;


}
