package com.funding.backend.domain.donation.entity;

import com.funding.backend.domain.donationMilestone.entity.DonationMilestone;
import com.funding.backend.domain.donationReward.entity.DonationReward;
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

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "git_address")
    private String gitAddress;  //깃허브 링크

    @Column(name = "deploy_address", nullable = false)
    private String deployAddress;   //배포 링크

    @Column(name = "app_store_address")
    private String appStoreAddress; //앱스토어 링크

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProjectSubCategory> projectSubCategories = new ArrayList<>();

    @OneToMany(mappedBy = "donation", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<DonationReward> donationRewardList = new ArrayList<>();

    @OneToMany(mappedBy = "donation", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<DonationMilestone> donationMilestoneList = new ArrayList<>();

}
