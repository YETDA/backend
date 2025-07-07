package com.funding.backend.domain.donationCategory.repository;

import com.funding.backend.domain.donationCategory.entity.DonationCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationCategoryRepository extends JpaRepository<DonationCategory,Long> {
}
