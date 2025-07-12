package com.funding.backend.domain.review.repository;

import com.funding.backend.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    boolean existsByPurchaseId(Long purchaseId);

    Optional<Review> findByPurchaseId(Long purchaseId);

    Page<Review> findByPurchaseProjectId(Long projectId, Pageable pageable);

}
