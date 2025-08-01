package com.funding.backend.domain.projectSubCategory.service;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.projectSubCategory.repository.ProjectSubRepository;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectSubCategoryService {
    private final ProjectSubRepository projectSubRepository;


  @Transactional
  public List<ProjectSubCategory> createProjectSubCategories(List<SubjectCategory> subjectCategories, Donation donation) {

    List<ProjectSubCategory> subCategories = subjectCategories.stream()
        .map(subjectCategory -> ProjectSubCategory.builder()
            .subjectCategory(subjectCategory)
            .donation(donation)
            .build())
        .collect(Collectors.toList());

    return projectSubRepository.saveAll(subCategories);
  }

}
