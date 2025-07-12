package com.funding.backend.domain.subjectCategory.entity;

import com.funding.backend.domain.projectSubCategory.entity.ProjectSubCategory;
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
@Table(name = "subject_category")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class SubjectCategory extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // PK

    @Column(name = "name", nullable = false)
    private String name; // 카테고리 이름


    @OneToMany(mappedBy = "subjectCategory", cascade = CascadeType.REMOVE, orphanRemoval = false)
    List<ProjectSubCategory> projectSubCategoryList = new ArrayList<>();
}
