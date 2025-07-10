package com.funding.backend.domain.projectSubCategory.service;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.projectSubCategory.repository.ProjectSubRepository;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
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
public class ProjectSubCategoryService {
    private final ProjectSubRepository projectSubRepository;

  public List<ProjectSubCategory> createAndLinkCategories(Donation donation, List<SubjectCategory> subjectCategories) {
    return subjectCategories.stream()
        .map(subCategory -> ProjectSubCategory.builder()
            .donation(donation)
            .subjectCategory(subCategory)
            .build())
        .collect(Collectors.toList());
  }
}
