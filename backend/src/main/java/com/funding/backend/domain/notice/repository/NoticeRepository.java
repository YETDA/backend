package com.funding.backend.domain.notice.repository;

import com.funding.backend.domain.notice.entity.Notice;
import com.funding.backend.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice,Long> {
    List<Notice> findByProjectOrderByCreatedAtDesc(Project project);
}
