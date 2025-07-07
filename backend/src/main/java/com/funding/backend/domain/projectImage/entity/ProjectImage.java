package com.funding.backend.domain.projectImage.entity;


import com.funding.backend.domain.project.entity.Project;
import com.funding.backend.global.auditable.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name="project_images")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString
public class ProjectImage extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank
    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column
    private String originalFilename;

    @Column
    private String storedFileName;


    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @Column(name = "image_order")
    private Integer imageOrder;


}
