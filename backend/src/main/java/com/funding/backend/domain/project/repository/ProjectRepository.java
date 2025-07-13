package com.funding.backend.domain.project.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    @Query("""
    SELECT p
    FROM Project p
    WHERE p.projectType = :projectType
        AND p.projectStatus = com.funding.backend.enums.ProjectStatus.RECRUITING
    ORDER BY SIZE(p.likeList) DESC
    """)
    Page<Project> findByProjectTypeOrderByLikesDesc(@Param("projectType") ProjectType projectType, Pageable pageable);

    @Query("""
    SELECT p
    FROM Project p
    WHERE p.projectStatus = com.funding.backend.enums.ProjectStatus.RECRUITING
    ORDER BY SIZE(p.likeList) DESC
    """)
    Page<Project> findAllByOrderByLikesDesc(Pageable pageable);

    @Query("""
    SELECT p
    FROM Project p
        LEFT JOIN p.orderList o
    WHERE p.projectStatus = com.funding.backend.enums.ProjectStatus.RECRUITING
    GROUP BY p.id
    ORDER BY COUNT(o) DESC
    """)
    Page<Project> findAllByOrderBySellingAmountDesc(Pageable pageable);

    @Query("""
    SELECT p
    FROM Project p
    LEFT JOIN p.orderList o
    WHERE p.projectType = :projectType
        AND p.projectStatus = com.funding.backend.enums.ProjectStatus.RECRUITING
    GROUP BY p.id
    ORDER BY COUNT(o) DESC
    """)
    Page<Project> findByProjectTypeOrderBySellingAmountDesc(@Param("projectType") ProjectType projectType, Pageable pageable);

    @Query("""
    SELECT p
    FROM Project p
    JOIN p.donation d
    LEFT JOIN p.orderList o
    WHERE p.projectStatus = com.funding.backend.enums.ProjectStatus.RECRUITING
        AND p.projectType = com.funding.backend.enums.ProjectType.DONATION
        AND d.priceGoal > 0
    GROUP BY p, d.priceGoal
    ORDER BY COALESCE(SUM(o.paidAmount), 0) / d.priceGoal DESC
    """)
    Page<Project> findAllByOrderByAchievementRateDesc(Pageable pageable);

    Page<Project> findAllByProjectStatusIn(Collection<ProjectStatus> projectStatuses, Pageable pageable);

    //부분 일치 검색
    Page<Project> findByTitleContaining(String title, Pageable pageable);

}
