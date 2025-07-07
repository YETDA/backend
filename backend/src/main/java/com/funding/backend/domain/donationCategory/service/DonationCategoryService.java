package com.funding.backend.domain.donationCategory.service;


import com.funding.backend.domain.donationCategory.entity.DonationCategory;
import com.funding.backend.domain.donationCategory.repository.DonationCategoryRepository;
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
public class DonationCategoryService {
    private final DonationCategoryRepository donationCategoryRepository;

    public DonationCategory findDonationCategoryById(Long id ){
      return donationCategoryRepository.findById(id).orElseThrow(
              ()-> new BusinessLogicException(ExceptionCode.DONATION_CATEGORY_NOT_FOUND)
      );
    }
}
