package com.funding.backend.domain.purchaseOption.repository;

import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOptionRepository extends JpaRepository<PurchaseOption,Long> {
    void deleteByPurchase(Purchase purchase);
}
