package com.funding.backend.domain.project.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;

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

    @Query("SELECT p FROM Project p WHERE p.projectType = :projectType AND p.projectStatus IN :statuses")
    Page<Project> findByTypeAndStatuses(
            @Param("projectType") ProjectType projectType,
            @Param("statuses") List<ProjectStatus> statuses,
            Pageable pageable
    );

    //부분 일치 검색
    @EntityGraph(attributePaths = {"projectImage", "user", "purchase", "donation"})
    Page<Project> findByTitleContaining(String title, Pageable pageable);

    @Modifying
    @Query("UPDATE Project p SET p.projectStatus = :newStatus WHERE p.projectStatus = :oldStatus")
    void updateProjectStatusByStatus(@Param("oldStatus") ProjectStatus oldStatus, @Param("newStatus") ProjectStatus newStatus);

    // 사용자가 가진 프로젝트 리스트 중 RECRUITING 또는 COMPLETED 상태인 프로젝트 조회
    Page<Project> findByUserIdAndProjectStatusIn(Long userId, List<ProjectStatus> statuses, Pageable pageable);

    // 사용자가 가진 프로젝트 중 RECRUITING 또는 COMPLETED 상태인 프로젝트의 전체 갯수 조회
    Long countByUserIdAndProjectStatusIn(Long userId, List<ProjectStatus> statuses);


    // 유저가 생성한 Donation, Project 프로젝트 수 (상태 제한 포함)
    Long countByUserIdAndProjectTypeAndProjectStatusIn(Long userId, ProjectType type, List<ProjectStatus> statuses);



}
