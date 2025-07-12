package com.funding.backend.domain.qna.repository;

import com.funding.backend.domain.qna.entity.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {

    List<Qna> findByProjectId(Long projectId);

    List<Qna> findByUserId(Long userId);

    // 페이징 처리를 위한 메서드 추가
    Page<Qna> findAll(Pageable pageable);
    Page<Qna> findByProjectId(Long projectId, Pageable pageable);
    Page<Qna> findByUserId(Long userId, Pageable pageable);
}