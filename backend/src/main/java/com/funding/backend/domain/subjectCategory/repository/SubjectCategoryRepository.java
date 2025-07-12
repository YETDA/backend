package com.funding.backend.domain.subjectCategory.repository;

import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectCategoryRepository extends JpaRepository<SubjectCategory,Long> {
}
