package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donation.dto.request.DonationProjectDetail;
import com.funding.backend.domain.mainCategory.entity.MainCategory;
import com.funding.backend.domain.mainCategory.service.MainCategoryService;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.projectSubCategory.service.ProjectSubCategoryService;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.domain.subjectCategory.service.SubjectCategoryService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import com.funding.backend.global.utils.s3.S3Uploader;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationService {

  private final DonationRepository donationRepository;
  private final S3Uploader s3Uploader;
  private final MainCategoryService mainCategoryService;
  private final SubjectCategoryService subjectCategoryService;
  private final ProjectSubCategoryService projectSubCategoryService;

  @Transactional
  public Donation createDonation(Project project, DonationProjectDetail dto){
    MainCategory mainCategory = mainCategoryService.findDonationCategoryById(dto.getMainCategoryId());
    List<ProjectSubCategory> projectSubCategories = List.of();
    List<Long> subCategoryIds = dto.getSubCategoryIds();
    if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
      List<SubjectCategory> subjectCategories = subjectCategoryService.findCategoriesByIds(subCategoryIds);
      projectSubCategories = projectSubCategoryService.createProjectSubCategories(subjectCategories);
    }
    Donation donation = Donation.builder()
        .project(project)
        .mainCategory(mainCategory)
        .projectSubCategories(projectSubCategories)
        .priceGoal(dto.getPriceGoal())
        .startDate(dto.getStartDate().atStartOfDay())
        .endDate(dto.getEndDate().atStartOfDay())
        .gitAddress(dto.getGitAddress())
        .deployAddress(dto.getDeployAddress())
        .build();
    return donationRepository.save(donation);
  }

  @Transactional
  public void updateDonation(Project project, DonationUpdateRequestDto dto) {
    Donation donation = findByProject(project);

    if (dto.getMainCategoryId() != null) {
      MainCategory mainCategory = mainCategoryService.findDonationCategoryById(dto.getMainCategoryId());
      donation.setMainCategory(mainCategory);
    }
    Optional.ofNullable(dto.getGitAddress())
        .ifPresent(donation::setGitAddress);

    donationRepository.save(donation);
  }

  public Donation findByProject(Project project){
    return donationRepository.findByProject(project)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
  }
  public Donation findByProject(Long donationId ){
    return donationRepository.findById(donationId)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
  }


}
