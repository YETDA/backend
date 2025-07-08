package com.funding.backend.domain.purchaseCategory.repository;

import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseCategoryRepository extends JpaRepository<PurchaseCategory,Long> {
}
