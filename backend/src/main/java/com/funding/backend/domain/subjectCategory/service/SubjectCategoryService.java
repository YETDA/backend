package com.funding.backend.domain.subjectCategory.service;


import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.domain.subjectCategory.repository.SubjectCategoryRepository;
import com.funding.backend.global.exception.BusinessLogicException;
import com.funding.backend.global.exception.ExceptionCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SubjectCategoryService {
  private final SubjectCategoryRepository subjectCategoryRepository;

  public List<SubjectCategory> findCategoriesByIds(List<Long> ids) {
    if (ids.size() > 2) {
      throw new BusinessLogicException(ExceptionCode.DONATION_INVALID_INPUT_VALUE, "상세 카테고리는 최대 2개까지 선택 가능합니다.");
    }
    List<SubjectCategory> foundCategories = subjectCategoryRepository.findAllById(ids);
    if (foundCategories.size() != ids.size()) {
      throw new BusinessLogicException(ExceptionCode.SUBJECT_CATEGORY_NOT_FOUND);
    }
    return foundCategories;
  }
}
