package com.funding.backend.domain.settlement.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.settlement.entity.Settlement;
import com.funding.backend.enums.ProjectType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    //특정 프로젝트에 대한 Settlement 중 가장 최근에 끝난 정산 하나 조회
    Optional<Settlement> findTopByProjectOrderByPeriodEndDesc(Project project);

    List<Settlement> findAllByProject(Project project);

    // 유저+타입 별 전체 요청 횟수
    long countByUserIdAndProject_ProjectType(
            Long userId,
            ProjectType projectType
    );

    // 타입별 정산 총 금액
    @Query("""
                SELECT COALESCE(SUM(s.payoutAmount), 0)
                FROM Settlement s
                WHERE s.user.id = :userId
                  AND s.project.projectType = :projectType
                  AND s.status = com.funding.backend.enums.SettlementStatus.COMPLETED
            """)
    Long sumPayoutByUserIdAndProjectType(
            @Param("userId") Long userId,
            @Param("projectType") ProjectType projectType
    );


}
