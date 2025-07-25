package com.funding.backend.domain.purchase.entity;

import com.funding.backend.domain.like.entity.Like;
import com.funding.backend.domain.purchaseCategory.entity.PurchaseCategory;
import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.domain.purchaseOption.entity.PurchaseOption;
import com.funding.backend.enums.ProvidingMethod;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name = "purchase")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Purchase extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "project_id", nullable = false, unique = true) // FK + unique 제약
    private Project project;

    @Column(name = "git_address", nullable = false)
    private String gitAddress;


    @Column(name = "average_delivery_time")
    private String averageDeliveryTime;

    @ManyToOne
    @JoinColumn(name = "purchase_category_id")
    private PurchaseCategory purchaseCategory;


    @OneToMany(mappedBy = "purchase", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<PurchaseOption> purchaseOptionList = new ArrayList<>();




}
