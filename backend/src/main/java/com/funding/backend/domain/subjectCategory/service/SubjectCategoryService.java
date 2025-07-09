package com.funding.backend.domain.subjectCategory.service;


import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.domain.subjectCategory.repository.SubjectCategoryRepository;
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
public class SubjectCategoryService {
    private final SubjectCategoryRepository mainCategoryRepository;

    public SubjectCategory findDonationCategoryById(Long id ){
      return mainCategoryRepository.findById(id).orElseThrow(
              ()-> new BusinessLogicException(ExceptionCode.DONATION_CATEGORY_NOT_FOUND)
      );
    }
}
