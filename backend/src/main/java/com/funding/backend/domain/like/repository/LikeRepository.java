package com.funding.backend.domain.like.repository;

import com.funding.backend.domain.like.entity.Like;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    Optional<Like> findByUserIdAndProjectId(Long userId, Long projectId);
    Page<Like> findByUserId(Long userId, Pageable pageable);
    int countByProjectId(Long projectId);
}
