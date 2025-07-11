package com.funding.backend.domain.notice.repository;

import com.funding.backend.domain.notice.entity.Notice;
import com.funding.backend.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<Notice,Long> {
    Page<Notice> findByProjectOrderByCreatedAtDesc(Project project, Pageable pageable);
}
