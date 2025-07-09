package com.funding.backend.domain.projectSubCategory.entity;

import com.funding.backend.domain.donation.entity.Donation;
import com.funding.backend.domain.subjectCategory.entity.SubjectCategory;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "project_subject")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class ProjectSubCategory extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // PK


    @ManyToOne
    @JoinColumn(name = "subjectCategory_id", nullable = false)
    private SubjectCategory subjectCategory;

    @ManyToOne
    @JoinColumn(name = "donation_id", nullable = false)
    private Donation donation;

}
