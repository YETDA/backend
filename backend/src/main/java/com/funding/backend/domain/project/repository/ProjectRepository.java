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

    @Query("SELECT p FROM Project p WHERE p.projectType = :projectType AND p.projectStatus = 'COMPLETED' ORDER BY SIZE(p.likeList) DESC")
    Page<Project> findByProjectTypeOrderByLikesDesc(@Param("projectType") ProjectType projectType, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.projectStatus = 'COMPLETED' ORDER BY SIZE(p.likeList) DESC")
    Page<Project> findAllByOrderByLikesDesc(Pageable pageable);

    @Query(
            value = """
                SELECT p.*
                FROM projects p
                LEFT JOIN orders o ON p.id = o.project_id
                WHERE p.project_status = 'COMPLETED'
                GROUP BY p.id
                ORDER BY COALESCE(SUM(o.paid_amount), 0) DESC
            """,
            countQuery = """
                SELECT COUNT(DISTINCT p.id)
                FROM projects p
                WHERE p.project_status = 'COMPLETED'
            """, nativeQuery = true)
    Page<Project> findAllByOrderBySellingAmountDesc(Pageable pageable);

    @Query(
            value = """
                SELECT p.*
                FROM projects p
                LEFT JOIN orders o ON p.id = o.project_id
                WHERE p.project_type = :projectType
                  AND p.project_status = 'COMPLETED'
                GROUP BY p.id
                ORDER BY COALESCE(SUM(o.paid_amount), 0) DESC
            """,
            countQuery = """
                SELECT COUNT(DISTINCT p.id)
                FROM projects p
                WHERE p.project_type = :projectType
                  AND p.project_status = 'COMPLETED'
            """, nativeQuery = true)
    Page<Project> findByProjectTypeOrderBySellingAmountDesc(@Param("projectType") ProjectType projectType, Pageable pageable);

    @Query(value = """
                SELECT p.*
                FROM projects p
                JOIN donations d ON p.id = d.project_id
                LEFT JOIN orders o ON p.id = o.project_id
                WHERE p.project_status = 'COMPLETED'
                AND p.project_type = 'DONATION'
                GROUP BY p.id, d.price_coal
                HAVING d.price_coal > 0
                ORDER BY COALESCE(SUM(o.paid_amount), 0) / d.price_coal DESC
                """,
            countQuery = """
                SELECT COUNT(DISTINCT p.id)
                FROM projects p
                JOIN donations d ON p.id = d.project_id
                WHERE p.project_status = 'COMPLETED'
                AND p.project_type = 'DONATION'
                AND d.price_coal > 0
                """,
            nativeQuery = true)
    Page<Project> findAllByOrderByAchievementRateDesc(Pageable pageable);
}
