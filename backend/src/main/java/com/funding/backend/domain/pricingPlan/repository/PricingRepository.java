package com.funding.backend.domain.pricingPlan.repository;

import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRepository extends JpaRepository<PricingPlan,Long> {
}
