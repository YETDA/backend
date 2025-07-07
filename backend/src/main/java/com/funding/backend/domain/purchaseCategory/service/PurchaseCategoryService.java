package com.funding.backend.domain.purchaseCategory.service;


import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.domain.purchaseCategory.repository.PurchaseCategoryRepository;
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
public class PurchaseCategoryService {
    private final PurchaseCategoryRepository purchaseCategoryRepository;


    public PurchaseCategory findPurchaseCategoryById(Long id ){
      return purchaseCategoryRepository.findById(id).orElseThrow(
              ()-> new BusinessLogicException(ExceptionCode.PURCHASE_CATEGORY_NOT_FOUND)
      );
    }
}
