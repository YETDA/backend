package com.funding.backend.domain.purchase.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchase.entity.Purchase;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    Optional<Purchase> findByProject(Project project);

    @Query("SELECT p FROM Purchase p JOIN p.purchaseOptionList po WHERE po.id = :purchaseOptionId")
    Optional<Purchase> findByPurchaseOptionId(@Param("purchaseOptionId") Long purchaseOptionId);

    @Query("SELECT p FROM Purchase p JOIN FETCH p.project pr JOIN FETCH pr.user u WHERE p.id = :purchaseId")
    Optional<Purchase> findByIdWithProjectAndUser(@Param("purchaseId") Long purchaseId);


}
