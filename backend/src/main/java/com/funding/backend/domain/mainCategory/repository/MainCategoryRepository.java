package com.funding.backend.domain.mainCategory.repository;

import com.funding.backend.domain.mainCategory.entity.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainCategoryRepository extends JpaRepository<MainCategory,Long> {
}
