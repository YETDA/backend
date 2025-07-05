package com.funding.backend.domain.option.repository;

import com.funding.backend.domain.option.entity.PurchaseOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<PurchaseOption,Long> {
}
