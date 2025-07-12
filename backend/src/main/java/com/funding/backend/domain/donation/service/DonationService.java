package com.funding.backend.domain.donation.service;

import com.funding.backend.domain.donation.dto.request.DonationUpdateRequestDto;
import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.donation.repository.DonationRepository;
import com.funding.backend.domain.donation.dto.request.DonationProjectDetail;
import com.funding.backend.domain.mainCategory.entity.MainCategory;
import com.funding.backend.domain.mainCategory.service.MainCategoryService;
import com.funding.backend.domain.project.dto.response.DonationProjectResponseDto;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.project.repository.ProjectRepository;
import com.funding.backend.domain.projectSubCategory.dto.request.ProjectSubRequestDto;
import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.projectSubCategory.service.ProjectSubCategoryService;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.domain.subjectCategory.service.SubjectCategoryService;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DonationService {

  private final ProjectRepository projectRepository;
  private final DonationRepository donationRepository;

  private final MainCategoryService mainCategoryService;
  private final SubjectCategoryService subjectCategoryService;
  private final ProjectSubCategoryService projectSubCategoryService;

  @Transactional
  public Donation createDonation(Project project, DonationProjectDetail dto) {

    MainCategory mainCategory = mainCategoryService.findDonationCategoryById(dto.getMainCategoryId());

    List<Long> subCategoryIds = dto.getSubCategoryIds();
    List<SubjectCategory> subjectCategories = new ArrayList<>();

    if (subCategoryIds != null && !subCategoryIds.isEmpty()) {
      subjectCategories = subjectCategoryService.findCategoriesByIds(subCategoryIds);
    }

    Donation donation = Donation.builder()
        .project(project)
        .mainCategory(mainCategory)
        .priceGoal(dto.getPriceGoal())
        .startDate(dto.getStartDate().atStartOfDay())
        .endDate(dto.getEndDate().atStartOfDay())
        .gitAddress(dto.getGitAddress())
        .deployAddress(dto.getDeployAddress())
        .build();

    Donation savedDonation = donationRepository.save(donation);

    if (!subjectCategories.isEmpty()) {
      List<ProjectSubCategory> projectSubCategories = projectSubCategoryService.createProjectSubCategories(subjectCategories, savedDonation);
      savedDonation.setProjectSubCategories(projectSubCategories);
    }

    return savedDonation;
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

  @Transactional
  public void deleteDonation(Long projectId){
    Project project = projectRepository.findById(projectId)
        .orElseThrow(()->new BusinessLogicException(ExceptionCode.PROJECT_NOT_FOUND));

    Donation donation = donationRepository.findById(findByProject(project).getId())
        .orElseThrow(()->new BusinessLogicException(ExceptionCode.PURCHASE_NOT_FOUND));

    donationRepository.delete(donation);
    projectRepository.delete(project);
  }

  public Donation findByProject(Project project){
    return donationRepository.findByProject(project)
        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DONATION_NOT_FOUND));
  }


  public DonationProjectResponseDto createDonationProjectResponse(Project project) {
    Donation detail = findByProject(project);

    List<ProjectSubRequestDto> projectSubDtos = detail.getProjectSubCategories().stream()
        .map(psc -> new ProjectSubRequestDto(
            psc.getSubjectCategory().getId(),
            psc.getSubjectCategory().getName()
        ))
        .collect(Collectors.toList());

    return new DonationProjectResponseDto(
        project, detail, projectSubDtos
    );
  }

}
