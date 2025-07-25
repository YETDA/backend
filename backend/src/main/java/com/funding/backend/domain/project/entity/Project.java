package com.funding.backend.domain.project.entity;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.like.entity.Like;
import com.funding.backend.domain.notice.entity.Notice;
import com.funding.backend.domain.order.entity.Order;
import com.funding.backend.domain.pricingPlan.entity.PricingPlan;
import com.funding.backend.domain.projectImage.entity.ProjectImage;
import com.funding.backend.domain.purchase.entity.Purchase;
import com.funding.backend.domain.settlement.entity.Settlement;
import com.funding.backend.domain.user.entity.User;
import com.funding.backend.enums.ProjectStatus;
import com.funding.backend.enums.ProjectType;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "projects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Project extends Auditable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "project_type")
    private ProjectType projectType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    //pricingPlan_id로 변경해야함
    @JoinColumn(name = "management_id")
    private PricingPlan pricingPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStatus projectStatus; // 프로젝트 상태

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "introduce", nullable = false)
    private String introduce;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<ProjectImage> projectImage = new ArrayList<>();

    @OneToOne(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Purchase purchase;

    @OneToOne(mappedBy = "project", cascade = CascadeType.REMOVE)
    private Donation donation;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<Notice> noticeList = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<Like> likeList = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<Order> orderList = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<Settlement> settlementList = new ArrayList<>();

}
