package com.funding.backend.domain.pricingPlan.entity;

import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pricing_plans")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class PricingPlan extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // PK

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "platform_fee", nullable = false)
    private Long platformFee;

    @Column(name = "patment_fee", nullable = false)
    private Long payment_fee;


    @OneToMany(mappedBy = "pricingPlan", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<Project> projectList = new ArrayList<>();




}