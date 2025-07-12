package com.funding.backend.domain.donation.entity;

import com.funding.backend.domain.mainCategory.entity.MainCategory;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
import com.funding.backend.global.auditable.Auditable;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "donation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Donation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "project_id", nullable = false, unique = true)

    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id", nullable = false)
    private MainCategory mainCategory;

    @Column(name = "price_goal")
    private Long priceGoal;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "git_address")
    private String gitAddress;

    @Column(name = "deploy_address", nullable = false)
    private String deployAddress;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProjectSubCategory> projectSubCategories = new ArrayList<>();

}
