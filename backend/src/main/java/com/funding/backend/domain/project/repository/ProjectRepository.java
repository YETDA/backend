package com.funding.backend.domain.project.repository;

import com.funding.backend.domain.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project,Long> {

    //부분 일치 검색
    Page<Project> findByTitleContaining(String title, Pageable pageable);

}
