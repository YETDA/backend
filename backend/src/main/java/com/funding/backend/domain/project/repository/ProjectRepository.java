package com.funding.backend.domain.project.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("SELECT p FROM Project p WHERE p.projectType = :projectType AND p.projectStatus = com.funding.backend.enums.ProjectStatus.COMPLETED ORDER BY SIZE(p.likeList) DESC")
    Page<Project> findByProjectTypeOrderByLikesDesc(@Param("projectType") ProjectType projectType, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.projectStatus = com.funding.backend.enums.ProjectStatus.COMPLETED ORDER BY SIZE(p.likeList) DESC")
    Page<Project> findAllByOrderByLikesDesc(Pageable pageable);
}
