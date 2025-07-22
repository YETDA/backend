package com.funding.backend.domain.settlement.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.settlement.entity.Settlement;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    //특정 프로젝트에 대한 Settlement 중 가장 최근에 끝난 정산 하나 조회
    Optional<Settlement> findTopByProjectOrderByPeriodEndDesc(Project project);

    List<Settlement> findAllByProject(Project project);

    long countByUserId(Long userId);

}
