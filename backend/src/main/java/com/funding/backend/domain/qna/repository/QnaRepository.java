package com.funding.backend.domain.qna.repository;

import com.funding.backend.domain.qna.entity.Qna;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnaRepository extends JpaRepository<Qna, Long> {
}
