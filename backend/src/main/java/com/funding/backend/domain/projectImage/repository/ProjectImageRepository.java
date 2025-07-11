package com.funding.backend.domain.projectImage.repository;

import com.funding.backend.domain.projectImage.entity.ProjectImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectImageRepository extends JpaRepository<ProjectImage,Long> {
}
