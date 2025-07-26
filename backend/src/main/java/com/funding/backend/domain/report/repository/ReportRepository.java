package com.funding.backend.domain.report.repository;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.report.entity.Report;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    //한 유저가 동일한 프로젝트에 신고 내역이 존재하는지 확인
    boolean existsByReporterAndProject(User reporter, Project project);

    //신고 승인된 프로젝트 확인
    boolean existsByProjectAndReportStatus(Project project, ReportStatus reportStatus);

    Optional<Report> findById(Long id);

    Page<Report> findByReporter(User reporter, Pageable pageable);
    Page<Report> findByReportStatus(ReportStatus status, Pageable pageable);
    Page<Report> findByProjectId(Long projectId, Pageable pageable);

    long count();
    long countByReportStatus(ReportStatus status);
}
