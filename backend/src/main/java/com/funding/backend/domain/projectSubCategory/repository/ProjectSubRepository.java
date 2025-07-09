package com.funding.backend.domain.projectSubCategory.repository;

import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectSubRepository extends JpaRepository<ProjectSubCategory,Long> {
}
