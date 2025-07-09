package com.funding.backend.domain.projectSubCategory.service;


import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.domain.projectSubCategory.repository.ProjectSubRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectSubService {
    private final ProjectSubRepository mainCategoryRepository;

    public ProjectSubCategory findDonationCategoryById(Long id ){
      return mainCategoryRepository.findById(id).orElseThrow(
              ()-> new BusinessLogicException(ExceptionCode.DONATION_CATEGORY_NOT_FOUND)
      );
    }
}
